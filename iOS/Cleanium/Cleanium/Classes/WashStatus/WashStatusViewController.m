//
//  WashStatusViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "WashStatusViewController.h"
#import "GetLoadData.h"
#import "DateHelper.h"
#import "TimeHelper.h"
#import "MBProgressHUD.h"
#import "ServiceClient.h"
#import "SlideNavigationController.h"

@interface WashStatusViewController ()

@end

@implementation WashStatusViewController

#define ORDER_COMPLETED @"ORDER_COMPLETED"
#define MODE_PROGRESS @"PROGRESS"
#define MODE_FAIL @"FAIL"

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
        
    self.title = @"WASH STATUS";
    
    [self setEmptyOrder];
    
    appDelegate = appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    [GetLoadData getLoadData:self isFinished:^(NSNumber* isFinishedLocal){
        NSDictionary *statusDictionary;
        for (PFObject *order in appDelegate.userOrders) {
            NSDictionary *pomStatusDictionary = [self getStatusDictionary:[order objectForKey:@"status"]];
            if (![[pomStatusDictionary objectForKey:@"showOnHistory"] boolValue]) {
                currentOrder = order;
                statusDictionary = pomStatusDictionary;
                break;
            }
        }
        
        NSMutableArray *statusesProgressArray = [self getStatusesProgressArray:statusDictionary];
        washStatusView.numberOfStatuses = statusesProgressArray.count;
        
        if (currentOrder) {
            NSInteger indexForCurrentStatus = [self findIndexForCurrentStatus:statusDictionary inArray:statusesProgressArray];
            [self showInfoForOrderStatus:statusDictionary withIndexForCurrentStatus:indexForCurrentStatus];
            
        }
        
        if ([UIApplication sharedApplication].applicationIconBadgeNumber > 0) {
            [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
        }
    }];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handlePlaceOrderNotification:)
                                                 name:@"PlaceOrderNotification"
                                               object:nil];
    
}

-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    if ([self.navigationController isNavigationBarHidden]) {
        [self.navigationController setNavigationBarHidden:NO];
    }

}

-(void)viewWillDisappear:(BOOL)animated{
    
    [super viewWillDisappear:animated];
    
    if ([[currentOrder objectForKey:@"status"] isEqualToString:ORDER_COMPLETED] && hasBeenToRating) {
        hasBeenToRating = NO;
        currentOrder = nil;
        [self setEmptyOrder];
    }
}

- (void)appDidBecomeActive:(NSNotification *)notification {
    NSLog(@"app did become active notification");
    if ([UIApplication sharedApplication].applicationIconBadgeNumber > 0) {
        if (currentOrder) {
            [self refreshOrder:currentOrder.objectId];
        }else{
            [self refreshAllOrders];
        }
    }
}

//-(void)viewWillDisappear:(BOOL)animated{
//    
//    [super viewWillDisappear:animated];
//    
//    if (timer) {
//        [timer invalidate];
//        timer = nil;
//    }
//}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - SlideNavigationController Methods -

- (BOOL)slideNavigationControllerShouldDisplayLeftMenu
{
    return YES;
}

-(void)refreshOrder:(NSString*)orderId{
    NSLog(@"order id %@", orderId);
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Loading";
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchOrderForIdWithCompletionBlock:^(id objects) {
        [hud hide:YES];
        currentOrder = [objects firstObject];
        
        if (currentOrder) {
            [self handleStatusChanged];
        }
        
        if ([UIApplication sharedApplication].applicationIconBadgeNumber > 0) {
            [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
        }
        
    } failureBlock:^(NSError *error) {
        [hud hide:YES];
    } withOrderId:orderId];
    
}

-(void)refreshAllOrders{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Loading";
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchOrdersWithCompletionBlock:^(id objects) {
        [hud hide:YES];
        
        appDelegate.userOrders = objects;
        
        NSDictionary *statusDictionary;
        for (PFObject *order in appDelegate.userOrders) {
            NSDictionary *pomStatusDictionary = [self getStatusDictionary:[order objectForKey:@"status"]];
            if (![[pomStatusDictionary objectForKey:@"showOnHistory"] boolValue]) {
                currentOrder = order;
                statusDictionary = pomStatusDictionary;
                break;
            }
        }
        
        NSMutableArray *statusesProgressArray = [self getStatusesProgressArray:statusDictionary];
        washStatusView.numberOfStatuses = statusesProgressArray.count;
        
        if (currentOrder) {
            NSInteger indexForCurrentStatus = [self findIndexForCurrentStatus:statusDictionary inArray:statusesProgressArray];
            [self showInfoForOrderStatus:statusDictionary withIndexForCurrentStatus:indexForCurrentStatus];
        }
        
        if ([UIApplication sharedApplication].applicationIconBadgeNumber > 0) {
            [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
        }
    } failureBlock:^(NSError *error) {
        [hud hide:YES];
    }];
}

-(void)clearStatusNotesViews{
    if (lblNotes) {
        [lblNotes removeFromSuperview];
    }
    if (costBreakdownView) {
        [costBreakdownView removeFromSuperview];
    }
    if (driverInfoView) {
        [driverInfoView removeFromSuperview];
    }
    if (btnCancel) {
        [btnCancel removeFromSuperview];
    }
    if (timer) {
        [timer invalidate];
        timer = nil;
    }
}

-(void)setEmptyOrder{
    NSString *statusText = [self getStatusText:[currentOrder objectForKey:@"status"]];
    [washStatusView drawStatus:StatusEmpty withStatusText:statusText];
    
    lblPickupDate.text = @"N/A";
    lblPickupTime.text = @"N/A";
    lblPickupDay.text = @"";
    
    lblDropOffDate.text = @"N/A";
    lblDropOffTime.text = @"N/A";
    lblDropOffDay.text = @"";
    
    [self clearStatusNotesViews];
    
    NSString *statusNotesText = [currentOrder objectForKey:@"notes"];
    if (statusNotesText) {
        [self createLblNotes];
        lblNotes.text = statusNotesText;
        [self addLblNotes];
    }
}

-(NSString*)getStatusText:(NSString*)status{
    
    NSString *statusText = @"No orders made";
    for (NSDictionary *statusDic in appDelegate.orderStatuses) {
        if ([[statusDic objectForKey:@"name"] isEqualToString:status]) {
            statusText = [statusDic objectForKey:@"text"];
            break;
        }
    }
    
    return statusText;
}

-(void)setDates{
    PFObject *fromDateObject = [currentOrder objectForKey:@"pickUpSchedule"];
    NSDate *fromDate = [fromDateObject objectForKey:@"fromDate"];
    NSDictionary *fromDateDictionary = [DateHelper toMM_ddStringFromDate:fromDate];
    
    lblPickupDate.text = [fromDateDictionary objectForKey:@"formatedDate"];
    lblPickupDay.text = [fromDateDictionary objectForKey:@"dayName"];
    lblPickupTime.text = [TimeHelper timeFromDate:fromDate];
    
    PFObject *toDateObject = [currentOrder objectForKey:@"deliverySchedule"];
    NSDate *toDate = [toDateObject objectForKey:@"toDate"];
    NSDictionary *toDateDictionary = [DateHelper toMM_ddStringFromDate:toDate];
    
    lblDropOffDate.text = [toDateDictionary objectForKey:@"formatedDate"];
    lblDropOffDay.text = [toDateDictionary objectForKey:@"dayName"];
    lblDropOffTime.text = [TimeHelper timeFromDate:toDate];
}

-(void)removeCancelButton:(id)sender{
    [timer invalidate];
    timer = nil;
    
    [btnCancel removeFromSuperview];
    [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[driverInfoView]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(driverInfoView, statusNotesContentView)]];
}

-(bool)canCancelOrder:(NSDate*)fromDate withTimeInterval:(NSNumber**)timeInterval{
    
    NSDate *pomCurrentDate = [NSDate date];
    NSTimeZone *tz = [NSTimeZone localTimeZone];
    NSInteger seconds = [tz secondsFromGMTForDate:pomCurrentDate];
    NSDate *currentDate = [NSDate dateWithTimeInterval: seconds sinceDate:pomCurrentDate];
    NSTimeInterval distanceBetweenDates = [fromDate timeIntervalSinceDate:currentDate];
    double secondsInAnHour = 3600;
    NSInteger hoursBetweenDates = distanceBetweenDates / secondsInAnHour;
    
    *timeInterval = [NSNumber numberWithFloat:distanceBetweenDates - (2 * secondsInAnHour)];
    
    return hoursBetweenDates >= 2;
}

-(void)createDriverInfo{
    if (!driverInfoView) {
        driverInfoView = [DriverInfoView new];
        driverInfoView.translatesAutoresizingMaskIntoConstraints = NO;
    }
}

-(void)createCancelBtn{
    if (!btnCancel) {
        btnCancel = [UIButton buttonWithType:UIButtonTypeCustom];
        btnCancel.translatesAutoresizingMaskIntoConstraints = NO;
        [btnCancel setTitle:@"Cancel" forState:UIControlStateNormal];
        [btnCancel setBackgroundColor:[UIColor lightGrayColor]];
        [btnCancel addTarget:self action:@selector(cancelOrder:) forControlEvents:UIControlEventTouchUpInside];
    }
}

-(void)createCostbreakdownView{
    if (!costBreakdownView) {
        costBreakdownView = [CostBreakdownView new];
        costBreakdownView.translatesAutoresizingMaskIntoConstraints = NO;
    }
}

-(void)createLblNotes{
    if (!lblNotes) {
        lblNotes = [UILabel new];
        lblNotes.translatesAutoresizingMaskIntoConstraints = NO;
        [lblNotes setFont:[UIFont fontWithName:@"HelveticaNeue" size:14]];
        [lblNotes setTextColor:[UIColor colorWithRed:105/255.0 green:105/255.0 blue:105/255.0 alpha:1]];
    }
}

-(void)addLblNotes{
    
    [statusNotesContentView addSubview:lblNotes];
    [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-8-[lblNotes]-8-|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(lblNotes, statusNotesContentView)]];
    [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-8-[lblNotes]-8-|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(lblNotes, statusNotesContentView)]];
    
}

-(void)cancelOrder:(id)sender{
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchCancelOrderForIdWithCompletionBlock:^(id orderObject) {
        
        currentOrder = orderObject;
        if (currentOrder) {
            [self handleStatusChanged];
        }
    } failureBlock:^(NSError *error) {
        
    } withOrderId:currentOrder.objectId withView:self.view];
}

- (void)handlePlaceOrderNotification:(NSNotification *)notification
{
    PFObject *object = notification.object;
    if (object) {
        currentOrder = object;
        [appDelegate.userOrders addObject:currentOrder];
        
        [self handleStatusChanged];
    }
}

-(void)fromRating{
    hasBeenToRating = YES;
}

-(NSDictionary*)getStatusDictionary:(NSString*)status{
    
    for (NSDictionary *statusDic in appDelegate.orderStatuses) {
        if ([[statusDic objectForKey:@"name"] isEqualToString:status]) {
            return statusDic;
        }
    }
    
    return nil;
}

-(NSMutableArray*)getStatusesProgressArray:(NSDictionary*)statusDictionary{
    
    NSMutableArray *statusesProgressArray = [[NSMutableArray alloc] init];
    if ([[statusDictionary objectForKey:@"mode"] isEqualToString:MODE_PROGRESS]) {
        for (NSDictionary *statusDic in appDelegate.orderStatuses) {
            if ([[statusDic objectForKey:@"mode"] isEqualToString:MODE_PROGRESS]) {
                [statusesProgressArray addObject:statusDic];
            }
        }
    }
    
    return statusesProgressArray;
}

-(NSInteger)findIndexForCurrentStatus:(NSDictionary*)statusDictionary inArray:(NSMutableArray*)statusesProgressArray{
    NSInteger indexForCurentStatus = StatusHold;
    for (int i = 0; i < statusesProgressArray.count; i++) {
        NSDictionary *pomStatusDictionary = [statusesProgressArray objectAtIndex:i];
        if ([[pomStatusDictionary objectForKey:@"name"] isEqualToString:[statusDictionary objectForKey:@"name"]]) {
            indexForCurentStatus = i;
        }
    }
    
    return indexForCurentStatus;
}

-(void)showInfoForOrderStatus:(NSDictionary*)statusDictionary withIndexForCurrentStatus:(NSInteger)indexForCurentStatus{
    [self clearStatusNotesViews];
    
    [washStatusView drawStatus:indexForCurentStatus withStatusText:[statusDictionary objectForKey:@"text"]];

    [self setDates];
    
    NSMutableArray *statusNotesViews = [[NSMutableArray alloc] init];
    
    NSString *statusNotesText = [currentOrder objectForKey:@"notes"];
    if (statusNotesText) {
        [self createLblNotes];
        lblNotes.text = statusNotesText;
        
        [statusNotesViews addObject:lblNotes];
    }
    
    PFObject *receiptObject = [currentOrder objectForKey:@"paymentReceipt"];
    if (receiptObject) {
        [self createAndShowPaymentReceipt:receiptObject];
        
        [statusNotesViews addObject:costBreakdownView];
    }
    
    NSString *driverFrom = [statusDictionary objectForKey:@"driverFrom"];
    if (driverFrom) {
        [self createAndShowDriver:driverFrom];
        
        [statusNotesViews addObject:driverInfoView];
    }
    
    bool isCancellable = [[statusDictionary objectForKey:@"cancellable"] boolValue];
    if (isCancellable) {
        PFObject *fromDateObject = [currentOrder objectForKey:@"pickUpSchedule"];
        NSDate *fromDate = [fromDateObject objectForKey:@"fromDate"];
        
        NSNumber *timeInterval;
        if ([self canCancelOrder:fromDate withTimeInterval:&timeInterval]) {
            [self createCancelBtn];
            timer = [NSTimer timerWithTimeInterval:[timeInterval floatValue] target:self selector:@selector(removeCancelButton:) userInfo:nil repeats:NO];
            [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
            
            [statusNotesViews addObject:btnCancel];
        }
    }
    
    if (statusNotesViews.count > 0) {
        [self drawStatusNotesViews:statusNotesViews];
    }
}

-(void)drawStatusNotesViews:(NSMutableArray*)statusNotesViews{
    UIView *prevView;
    for (int i = 0; i < statusNotesViews.count; i++) {
        UIView *statusView = [statusNotesViews objectAtIndex:i];
        [statusView setTranslatesAutoresizingMaskIntoConstraints:NO];
        [statusNotesContentView addSubview:statusView];
        
        NSDictionary *metrics   = @{ @"space":@(8), @"width":@(statusNotesContentView.frame.size.width - 16)};
        if ([statusView isKindOfClass:[UIButton class]]) {
            metrics   = @{ @"space":@(0), @"width":@(statusNotesContentView.frame.size.width)};
        }
        
        if (i == 0) {
            [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-8-[statusView]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(statusView, statusNotesContentView)]];
            prevView = statusView;
        }else{
            [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[prevView]-8-[statusView]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(statusView, prevView)]];
            prevView = statusView;
        }
        [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-space-[statusView(==width)]-space-|" options:NSLayoutFormatAlignAllLeading metrics:metrics views:NSDictionaryOfVariableBindings(statusView, statusNotesContentView)]];
        if (i == statusNotesViews.count - 1) {
            [statusNotesContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[prevView]-space-|" options:0 metrics:metrics views:NSDictionaryOfVariableBindings(statusView, prevView)]];
        }
        
    }
}

-(void)createAndShowDriver:(NSString*)driverFrom{
    [self createDriverInfo];
    PFObject *driverObject = [currentOrder objectForKey:driverFrom];
    PFUser *driverUser = [driverObject objectForKey:@"user"];
    
    driverInfoView.lblName.text = [NSString stringWithFormat:@"%@ %@", [driverUser objectForKey:@"fname"], [driverUser objectForKey:@"lname"]];
    
    PFFile *driverImageFile = [driverUser objectForKey:@"image"];
    [driverImageFile getDataInBackgroundWithBlock:^(NSData *data, NSError *error) {
        if (!error) {
            driverInfoView.imgView.image = [UIImage imageWithData:data];
        }
    }];
    
}

-(void)createAndShowPaymentReceipt:(PFObject*)receiptObject{
    NSNumber *totalAmount = [receiptObject objectForKey:@"total"];
    NSArray *services = [receiptObject objectForKey:@"services"];
    [self createCostbreakdownView];
    [costBreakdownView setServices:services andTotalAmount:totalAmount];
}

-(void)handleStatusChanged{
    NSDictionary *statusDictionary = [self getStatusDictionary:[currentOrder objectForKey:@"status"]];
    NSMutableArray *statusesProgressArray = [self getStatusesProgressArray:statusDictionary];
    washStatusView.numberOfStatuses = statusesProgressArray.count;
    NSInteger indexForCurrentStatus = [self findIndexForCurrentStatus:statusDictionary inArray:statusesProgressArray];
    if (indexForCurrentStatus == statusesProgressArray.count - 1) {
        indexForCurrentStatus++;
        RatingViewController *ratingViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"RatingViewController"];
        ratingViewController.order = currentOrder;
        ratingViewController.delegate = self;
        [[SlideNavigationController sharedInstance] pushViewController:ratingViewController animated:YES];
    }
    [self showInfoForOrderStatus:statusDictionary withIndexForCurrentStatus:indexForCurrentStatus];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
