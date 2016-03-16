//
//  ScheduleViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/29/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "ScheduleViewController.h"
#import "ActionSheetCustomPicker.h"
#import "MBProgressHUD.h"
#import "ServiceClient.h"
#import "TimeHelper.h"

@interface ScheduleViewController ()

@end

@implementation ScheduleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"SCHEDULE A PICKUP";
    
    appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    [self getScheduleData];
    
    enteredColor = [UIColor colorWithRed:105/255.0f green:105/255.0f blue:105/255.0f alpha:1];
    emptyColor = [UIColor colorWithRed:199/255.0f green:199/255.0f blue:205/255.0f alpha:1];
    
    lblHome.text = @"Please choose location";
    lblAddress.text = @"";
    
    locationSelectedIndex = 0;
    
    [self resetPickupAndDropOff];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardDidHideNotification object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - SlideNavigationController Methods -

- (BOOL)slideNavigationControllerShouldDisplayLeftMenu
{
    return YES;
}

-(void)getScheduleData{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Loading";
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchScheduleDataWithCompletionBlock:^(id object) {
        [hud hide:YES];
        
        scheduleData = object;
        
//        if (scheduleData) {
//            NSDate *maxPickUpDate = [DateHelper dateTo7DayAhead:scheduleData.minPickUpDate];
//            calcDate = [DateHelper calculateDates:scheduleData.minPickUpDate andMaxDate:maxPickUpDate];
//        }
        
    } failureBlock:^(NSError *error) {
        [hud hide:YES];
        
    }];
}

-(void)resetPickupAndDropOff{
    
    lblPickupDayMonth.text = @"Pick a Date";
    [lblPickupDayMonth setTextColor:emptyColor];
    [lblPickupDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:17]];
    lblPickupDay.text = @"";
    pickUpDaySelectedIndex = 0;
    pickUpMonthSelectedIndex = 0;
    
    lblPickupTime.text = @"Pick Time";
    [lblPickupTime setTextColor:emptyColor];
    pickUpTimeSelectedIndex = 0;
    
    selectedPickupDateTime = nil;
    timePickUpArray = nil;
    
    lblDropOffDayMonth.text = @"Pick a Date";
    [lblDropOffDayMonth setTextColor:emptyColor];
    [lblDropOffDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:17]];
    lblDropOffDay.text = @"";
    dropOffDaySelectedIndex = 0;
    dropOffMonthSelectedIndex = 0;
    
    lblDropOffTime.text = @"Pick Time";
    [lblDropOffTime setTextColor:emptyColor];
    dropOffTimeSelectedIndex = 0;
    
    selectedDropOffDateTime = nil;
    timeDropOffArray = nil;
}

-(IBAction)locationAction:(id)sender{
    
    if ([txtNotes isFirstResponder]) {
        [txtNotes resignFirstResponder];
    }
    
    if (appDelegate.userAddresses.count > 0) {
        LocationCustomPicker *locationCustomPicker = [[LocationCustomPicker alloc] init];
        locationCustomPicker.delegate = self;
        
        NSNumber *selIndex = [NSNumber numberWithInteger:locationSelectedIndex];
        
        NSArray *initialSelections = @[selIndex];
        
        [ActionSheetCustomPicker showPickerWithTitle:@"Select Location" delegate:locationCustomPicker showCancelButton:YES origin:sender
                                   initialSelections:initialSelections];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"No address" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
    
}

-(void)locationPickerDone:(PFObject *)selectedAddress withIndex:(NSInteger)index{
    
    NSLog(@"opa done");
    if (selectedAddress == nil && appDelegate.userAddresses.count > 0) {
        selectedAddress = [appDelegate.userAddresses objectAtIndex:locationSelectedIndex];
//        locationSelectedIndex = 0;
    }
    lblHome.text = [selectedAddress objectForKey:@"location"];
    [lblHome setTextColor:enteredColor];
    lblAddress.text = [NSString stringWithFormat:@"%@, %@", [selectedAddress objectForKey:@"street"], [selectedAddress objectForKey:@"streetTwo"]];
    locationSelectedIndex = index;
    selectedPostCode = [selectedAddress objectForKey:@"postCode"];
    
    [self resetPickupAndDropOff];
}

-(IBAction)switchAction:(id)sender{
    
//    [self resetPickupAndDropOff];
    if (selectedPickupDateTime) {
        dropOffDateDictionary = [DateHelper calculateDropOffDate:selectedPickupDateTime andMaxDate:scheduleData.maxDeliveryDate isOtherService:swServices.on];
        
        
        if ([DateHelper isSelectedDropOffDateValid:selectedDropOffDateTime withSelectedPickUpDate:selectedPickupDateTime isOtherService:swServices.on]) {
            NSLog(@"validan");
            
            [self checkIndexesForValidatedDropOff];
            
            NSString *selectedDropOffDateString = [DateHelper toYYYY_M_D_DateStringFromDate:selectedDropOffDateTime];
            NSDictionary *dayDictionary = [scheduleData.reservedDictionary objectForKey:selectedPostCode];
            NSArray *reservedTimeBlocks = [dayDictionary objectForKey:selectedDropOffDateString];
            timeDropOffArray = [TimeHelper calculateTimeBlocksForDate:[self isEqualDatesDropOff:selectedDropOffDateString] withReservedTimeBlocks:reservedTimeBlocks withMaxDate:scheduleData.maxDeliveryDate];
            
            NSNumber *indexFound;
            if ([TimeHelper isTimeValid:selectedDropOffDateTime withTimeArray:timeDropOffArray withIndexFound:&indexFound]) {
                NSLog(@"time validan");
                
                dropOffTimeSelectedIndex = [indexFound integerValue];
            }else{
                NSLog(@"time nije validan");
                
                [self resetDropOffDateTime];
            }
        }else{
            NSLog(@"nije validan");
            
            [self resetDropOffDateTime];
        }
    }
}

-(IBAction)dayMonthAction:(id)sender{
    
    if (selectedPostCode) {
        if (scheduleData) {
            NSDate *minPickUpDate = [DateHelper getMinPickUpDate:scheduleData.minPickUpDate];
            NSDate *maxPickUpDate = [DateHelper dateTo7DayAhead:scheduleData.minPickUpDate];
            calcDate = [DateHelper calculateDates:minPickUpDate andMaxDate:maxPickUpDate];
        }
        if (calcDate.calcDateDictionary.allKeys.count > 0) {
            DayMonthCustomPicker *dayMonthCustomPicker = [[DayMonthCustomPicker alloc] init];
            dayMonthCustomPicker.delegate = self;
            dayMonthCustomPicker.type = @"pickUp";
            //    dayMonthCustomPicker.minYear = calcDate.calcMinYear;
            //    dayMonthCustomPicker.maxYear = calcDate.calcMaxYear;
            //    dayMonthCustomPicker.selectedYear = dayMonthCustomPicker.minYear;
            dayMonthCustomPicker.selectedMonthIndex = pickUpMonthSelectedIndex;
            dayMonthCustomPicker.selectedDayIndex = pickUpDaySelectedIndex;
            [dayMonthCustomPicker setDateArrays:calcDate.calcDateDictionary];
            
            NSNumber *yass1 = [NSNumber numberWithInteger:pickUpMonthSelectedIndex];
            NSNumber *yass2 = [NSNumber numberWithInteger:pickUpDaySelectedIndex];;
            
            NSArray *initialSelections = @[yass1, yass2];
            
            [ActionSheetCustomPicker showPickerWithTitle:@"Select date" delegate:dayMonthCustomPicker showCancelButton:YES origin:sender
                                       initialSelections:initialSelections];
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"No dates" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
            [alert show];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose location" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
    
}

-(void)pickupPickerDone:(NSString *)selectedDate withMonthIndex:(NSInteger)monthIndex withDayIndex:(NSInteger)dayIndex withType:(NSString *)type{
    
    if ([type isEqualToString:@"pickUp"]) {
        pickUpMonthSelectedIndex = monthIndex;
        pickUpDaySelectedIndex = dayIndex;
        
        NSDictionary *formattedDate = [DateHelper stringWithDateFormat:selectedDate];
        lblPickupDayMonth.text = [formattedDate objectForKey:@"formatedDate"];
        [lblPickupDayMonth setTextColor:enteredColor];
        [lblPickupDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:23]];
        lblPickupDay.text = [formattedDate objectForKey:@"dayName"];
        
        NSDictionary *dayDictionary = [scheduleData.reservedDictionary objectForKey:selectedPostCode];
        NSArray *reservedTimeBlocks = [dayDictionary objectForKey:selectedDate];
        NSDate *minPickUpDate = [DateHelper getMinPickUpDate:scheduleData.minPickUpDate];
        timePickUpArray = [TimeHelper calculateTimeBlocksForDate:[self isEqualDates:selectedDate andSecondDate:minPickUpDate] withReservedTimeBlocks:reservedTimeBlocks withMaxDate:scheduleData.maxDeliveryDate];
        lblPickupTime.text = @"Pick Time";
        [lblPickupTime setTextColor:emptyColor];
        pickUpTimeSelectedIndex = 0;
        selectedPickupDateTime = nil;
        
        lblDropOffDayMonth.text = @"Pick a Date";
        [lblDropOffDayMonth setTextColor:emptyColor];
        [lblDropOffDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:17]];
        lblDropOffDay.text = @"";
        
        lblDropOffTime.text = @"Pick Time";
        [lblDropOffTime setTextColor:emptyColor];
        dropOffTimeSelectedIndex = 0;
        selectedDropOffDateTime = nil;
        timeDropOffArray = nil;
    }else if ([type isEqualToString:@"dropOff"]) {
        dropOffMonthSelectedIndex = monthIndex;
        dropOffDaySelectedIndex = dayIndex;
        
        NSDictionary *formattedDate = [DateHelper stringWithDateFormat:selectedDate];
        lblDropOffDayMonth.text = [formattedDate objectForKey:@"formatedDate"];
        [lblDropOffDayMonth setTextColor:enteredColor];
        [lblDropOffDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:23]];
        lblDropOffDay.text = [formattedDate objectForKey:@"dayName"];
        
        NSDictionary *dayDictionary = [scheduleData.reservedDictionary objectForKey:selectedPostCode];
        NSArray *reservedTimeBlocks = [dayDictionary objectForKey:selectedDate];
        timeDropOffArray = [TimeHelper calculateTimeBlocksForDate:[self isEqualDatesDropOff:selectedDate] withReservedTimeBlocks:reservedTimeBlocks withMaxDate:scheduleData.maxDeliveryDate];
        lblDropOffTime.text = @"Pick Time";
        [lblDropOffTime setTextColor:emptyColor];
        dropOffTimeSelectedIndex = 0;
        selectedDropOffDateTime = nil;
    }
    
}

-(NSDate*)isEqualDates:(NSString*)firstDate andSecondDate:(NSDate*)secondDate{
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-M-d"];
    NSString *dateString = [df stringFromDate:secondDate];
    
    if ([firstDate isEqualToString:dateString]) {
        return secondDate;
    }
    
    NSDate *date = [df dateFromString:firstDate];
    
    return date;
}

-(NSDate*)isEqualDatesDropOff:(NSString*)firstDate{
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-M-d"];
    NSString *dateString = [df stringFromDate:[dropOffDateDictionary objectForKey:@"minDate"]];
    
    if ([firstDate isEqualToString:dateString]) {
        return [dropOffDateDictionary objectForKey:@"minDate"];
    }
    
    NSDate *date = [df dateFromString:firstDate];
    
    return date;
}

-(IBAction)dayMonthDropOffAction:(id)sender{
    
    if (selectedPostCode == nil ) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose location" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (selectedPickupDateTime) {
        CalculatedDate *dropOffCalcDate = [DateHelper calculateDates:[dropOffDateDictionary objectForKey:@"minDate"] andMaxDate:[dropOffDateDictionary objectForKey:@"maxDate"]];
        
        if (dropOffCalcDate.calcDateDictionary.allKeys.count > 0) {
            DayMonthCustomPicker *dayMonthCustomPicker = [[DayMonthCustomPicker alloc] init];
            dayMonthCustomPicker.delegate = self;
            dayMonthCustomPicker.type = @"dropOff";
            dayMonthCustomPicker.selectedMonthIndex = dropOffMonthSelectedIndex;
            dayMonthCustomPicker.selectedDayIndex = dropOffDaySelectedIndex;
            [dayMonthCustomPicker setDateArrays:dropOffCalcDate.calcDateDictionary];
            
            NSNumber *yass1 = [NSNumber numberWithInteger:dropOffMonthSelectedIndex];
            NSNumber *yass2 = [NSNumber numberWithInteger:dropOffDaySelectedIndex];;
            
            NSArray *initialSelections = @[yass1, yass2];
            
            [ActionSheetCustomPicker showPickerWithTitle:@"Select date" delegate:dayMonthCustomPicker showCancelButton:YES origin:sender
                                       initialSelections:initialSelections];
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"No dates" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
            [alert show];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose pickup date and time" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}

-(IBAction)timePickupAction:(id)sender{
    
    if (selectedPostCode == nil ) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose location" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (timePickUpArray.count > 0) {
        TimeCustomPicker *timeCustomPicker = [[TimeCustomPicker alloc] init];
        timeCustomPicker.delegate = self;
        timeCustomPicker.type = @"pickUp";
        timeCustomPicker.selectedTimeIndex = pickUpTimeSelectedIndex;
        [timeCustomPicker setTimeArrays:timePickUpArray];
        
        NSNumber *yass1 = [NSNumber numberWithInteger:pickUpTimeSelectedIndex];
        
        NSArray *initialSelections = @[yass1];
        
        [ActionSheetCustomPicker showPickerWithTitle:@"Select time" delegate:timeCustomPicker showCancelButton:YES origin:sender
                                   initialSelections:initialSelections];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose pickup date" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)timePickerDone:(NSDate *)selectedTime witTimeIndex:(NSInteger)timeIndex withType:(NSString *)type{
    if ([type isEqualToString:@"pickUp"]) {
        selectedPickupDateTime = selectedTime;
        pickUpTimeSelectedIndex = timeIndex;
        lblPickupTime.text = [TimeHelper timeFromDate:selectedTime];
        [lblPickupTime setTextColor:enteredColor];
        
        dropOffDateDictionary = [DateHelper calculateDropOffDate:selectedTime andMaxDate:scheduleData.maxDeliveryDate isOtherService:swServices.on];
        
        
        if ([DateHelper isSelectedDropOffDateValid:selectedDropOffDateTime withSelectedPickUpDate:selectedPickupDateTime isOtherService:swServices.on]) {
            NSLog(@"validan");
            
            [self checkIndexesForValidatedDropOff];
            
            NSString *selectedDropOffDateString = [DateHelper toYYYY_M_D_DateStringFromDate:selectedDropOffDateTime];
            NSDictionary *dayDictionary = [scheduleData.reservedDictionary objectForKey:selectedPostCode];
            NSArray *reservedTimeBlocks = [dayDictionary objectForKey:selectedDropOffDateString];
            timeDropOffArray = [TimeHelper calculateTimeBlocksForDate:[self isEqualDatesDropOff:selectedDropOffDateString] withReservedTimeBlocks:reservedTimeBlocks withMaxDate:scheduleData.maxDeliveryDate];
            
            NSNumber *indexFound;
            if ([TimeHelper isTimeValid:selectedDropOffDateTime withTimeArray:timeDropOffArray withIndexFound:&indexFound]) {
                NSLog(@"time validan");
                
                dropOffTimeSelectedIndex = [indexFound integerValue];
            }else{
                NSLog(@"time nije validan");
                
                [self resetDropOffDateTime];
            }
        }else{
            NSLog(@"nije validan");
            
            [self resetDropOffDateTime];
        }
        
    }else if ([type isEqualToString:@"dropOff"]) {
        selectedDropOffDateTime = selectedTime;
        dropOffTimeSelectedIndex = timeIndex;
        lblDropOffTime.text = [TimeHelper timeFromDate:selectedTime];
        [lblDropOffTime setTextColor:enteredColor];
    }
}

-(void)resetDropOffDateTime{
    dropOffMonthSelectedIndex = 0;
    dropOffDaySelectedIndex = 0;
    
    lblDropOffDayMonth.text = @"Pick a Date";
    [lblDropOffDayMonth setTextColor:emptyColor];
    [lblDropOffDayMonth setFont:[UIFont fontWithName:@"HelveticaNeue" size:17]];
    lblDropOffDay.text = @"";
    
    lblDropOffTime.text = @"Pick Time";
    [lblDropOffTime setTextColor:emptyColor];
    dropOffTimeSelectedIndex = 0;
    selectedDropOffDateTime = nil;
    timeDropOffArray = nil;
}

-(IBAction)timeDropOffAction:(id)sender{
    
    if (selectedPostCode == nil ) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose location" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (selectedPickupDateTime == nil){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose pickup date and time" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (timeDropOffArray.count > 0) {
        TimeCustomPicker *timeCustomPicker = [[TimeCustomPicker alloc] init];
        timeCustomPicker.delegate = self;
        timeCustomPicker.type = @"dropOff";
        timeCustomPicker.selectedTimeIndex = dropOffTimeSelectedIndex;
        [timeCustomPicker setTimeArrays:timeDropOffArray];
        
        NSNumber *yass1 = [NSNumber numberWithInteger:dropOffTimeSelectedIndex];
        
        NSArray *initialSelections = @[yass1];
        
        [ActionSheetCustomPicker showPickerWithTitle:@"Select time" delegate:timeCustomPicker showCancelButton:YES origin:sender
                                   initialSelections:initialSelections];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose drop off date" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)checkIndexesForValidatedDropOff{
    
    CalculatedDate *dropOffCalcDate = [DateHelper calculateDates:[dropOffDateDictionary objectForKey:@"minDate"] andMaxDate:[dropOffDateDictionary objectForKey:@"maxDate"]];
    
    if (dropOffCalcDate.calcDateDictionary.allKeys.count > 0) {
        NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
        
        NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
        [gregorianCalendar setTimeZone:timeZone];
        NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit
                                                            fromDate:selectedDropOffDateTime];
        
        NSString *selectedMonth = [NSString stringWithFormat:@"%li", (long)components.month];
        NSArray *monthArray = [self sortMonths:dropOffCalcDate.calcDateDictionary];
        for (int i = 0; i < monthArray.count; i++) {
            NSString *month = [monthArray objectAtIndex:i];
            if ([month isEqualToString:selectedMonth]) {
                dropOffMonthSelectedIndex = i;
                break;
            }
        }
        
        NSDictionary *dic = [dropOffCalcDate.calcDateDictionary objectForKey:selectedMonth];
        NSArray *dayArray = [dic objectForKey:@"days"];
         NSString *selectedDay = [NSString stringWithFormat:@"%li", (long)components.day];
        for (int i = 0; i < dayArray.count; i++) {
            NSString *day = [dayArray objectAtIndex:i];
            if ([day isEqualToString:selectedDay]) {
                dropOffDaySelectedIndex = i;
                break;
            }
        }
    }else{
        [self resetDropOffDateTime];
    }
}

-(NSArray*)sortMonths:(NSDictionary*)dateDictionary{
    
    NSArray *sorted = [dateDictionary keysSortedByValueUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        if ([obj1 isKindOfClass:[NSDictionary class]] && [obj2 isKindOfClass:[NSDictionary class]]) {
            NSDictionary *d1 = obj1;
            NSDictionary *d2 = obj2;
            
            if ([[d1 objectForKey:@"orderNumber"] intValue] > [[d2 objectForKey:@"orderNumber"] intValue]) {
                return (NSComparisonResult)NSOrderedDescending;
            } else if ([[d1 objectForKey:@"orderNumber"] intValue] < [[d2 objectForKey:@"orderNumber"] intValue]) {
                return (NSComparisonResult)NSOrderedAscending;
            }
        }
        return (NSComparisonResult)NSOrderedSame;
        
    }];

    return sorted;
}

-(IBAction)placeOrderAction:(id)sender{
    if (selectedPostCode == nil ) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose location" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (selectedPickupDateTime == nil){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose pickup" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if (selectedDropOffDateTime == nil){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please choose drop off" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else if(![self areAllPaymentsCompleted]){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"You have a pending order. You can not make multiple orders. Please try again after completion of the current order" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }else{
        NSNumber *isStripeClient = [[PFUser currentUser] objectForKey:@"isStripeClient"];
        if (![isStripeClient boolValue]) {
            [CustomPopUp showPaymenInfo:self];
        }else{
            [self placeOrder];
        }
    }
}

-(bool)areAllPaymentsCompleted{
    
    bool areAllPaymentsCompleted = YES;
    for (PFObject *order in appDelegate.userOrders) {
        NSDictionary *pomStatusDictionary = [self getStatusDictionary:[order objectForKey:@"status"]];
        if (![[pomStatusDictionary objectForKey:@"showOnHistory"] boolValue]) {
            areAllPaymentsCompleted = NO;
            break;
        }
    }
    
    return areAllPaymentsCompleted;
}

-(NSDictionary*)getStatusDictionary:(NSString*)status{
    
    for (NSDictionary *statusDic in appDelegate.orderStatuses) {
        if ([[statusDic objectForKey:@"name"] isEqualToString:status]) {
            return statusDic;
        }
    }
    
    return nil;
}

-(void)okPaymentInfo{
    NSLog(@"ok from payment");
    [[PFUser currentUser] fetchInBackground];
    [self placeOrder];
//    [[PFUser currentUser] setObject:[NSNumber numberWithBool:YES] forKey:@"isStripeClient"];
//    [[PFUser currentUser] saveInBackground];
}

-(void)placeOrder{
    
    PFObject *pickUpSchedule = [PFObject objectWithClassName:@"DriverSchedule"];
    [pickUpSchedule setObject:selectedPickupDateTime forKey:@"fromDate"];
    [pickUpSchedule setObject:[DateHelper toDateFromDate:selectedPickupDateTime] forKey:@"toDate"];
    
    NSMutableDictionary *pickUpScheduleDic = [[NSMutableDictionary alloc] init];
    [pickUpScheduleDic setObject:[DateHelper toDateStringFromDate:selectedPickupDateTime] forKey:@"fromDate"];
    [pickUpScheduleDic setObject:[DateHelper toDateFromDate:selectedPickupDateTime] forKey:@"toDate"];
    
    PFObject *deliverySchedule = [PFObject objectWithClassName:@"DriverSchedule"];
    [deliverySchedule setObject:selectedDropOffDateTime forKey:@"fromDate"];
    [deliverySchedule setObject:[DateHelper toDateFromDate:selectedDropOffDateTime] forKey:@"toDate"];
    
    NSMutableDictionary *deliveryScheduleDic = [[NSMutableDictionary alloc] init];
    [deliveryScheduleDic setObject:[DateHelper toDateStringFromDate:selectedDropOffDateTime] forKey:@"fromDate"];
    [deliveryScheduleDic setObject:[DateHelper toDateFromDate:selectedDropOffDateTime] forKey:@"toDate"];
    
    PFObject *order = [PFObject objectWithClassName:@"Order"];
    [order setObject:[appDelegate.userAddresses objectAtIndex:locationSelectedIndex] forKey:@"pickUpAddress"];
    [order setObject:[appDelegate.userAddresses objectAtIndex:locationSelectedIndex] forKey:@"deliveryAddress"];
    [order setObject:[PFUser currentUser] forKey:@"user"];
    [order setObject:pickUpSchedule forKey:@"pickUpSchedule"];
    [order setObject:deliverySchedule forKey:@"deliverySchedule"];
    
    PFObject *address = [appDelegate.userAddresses objectAtIndex:locationSelectedIndex];
    NSMutableDictionary *addressDic = [[NSMutableDictionary alloc] init];
    [addressDic setObject:address.objectId forKey:@"objectId"];
    [addressDic setObject:[address objectForKey:@"postCode"] forKey:@"postCode"];
    
    NSMutableDictionary *orderDic = [[NSMutableDictionary alloc] init];
    [orderDic setObject:addressDic forKey:@"pickUpAddress"];
    [orderDic setObject:addressDic forKey:@"deliveryAddress"];
    [orderDic setObject:pickUpScheduleDic forKey:@"pickUpSchedule"];
    [orderDic setObject:deliveryScheduleDic forKey:@"deliverySchedule"];
    
    NSDictionary *discountCodeDic = [[NSUserDefaults standardUserDefaults] objectForKey:@"discountCode"];
    if (discountCodeDic) {
        NSString *code = [discountCodeDic objectForKey:@"code"];
        if (code) {
            [orderDic setObject:code forKey:@"discountCode"];
        }
    }
    
    [orderDic setObject:[NSNumber numberWithBool:swServices.on] forKey:@"washAndDry"];
    
    if (txtNotes.text.length > 0 && ![txtNotes.text isEqualToString:@"i.e. use scent-free detergent"]) {
        [orderDic setObject:txtNotes.text forKey:@"notes"];
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:orderDic
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    
    NSString *orderJson;
    if (!jsonData) {
    } else {
        orderJson = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    
    [self performPlaceOrder:orderJson];
    
//    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
//    hud.labelText = @"Place order";
//    
//    [order saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
//     {
//         [hud hide:YES];
//         if (error)
//         {
//             UIAlertView *alertView =
//             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
//                                        message:error.localizedDescription
//                                       delegate:self
//                              cancelButtonTitle:nil
//                              otherButtonTitles:@"Ok", nil];
//             [alertView show];
//         } else {
//             NSLog(@"success");
//             
//         }
//     }];
}

-(void)performPlaceOrder:(NSString*)placeOrderJSONString{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Place order";
    
    [PFCloud callFunctionInBackground:@"createOrder"
                       withParameters:@{@"order": placeOrderJSONString}
                                block:^(PFObject *response, NSError *error) {
                                    [hud hide:YES];
                                    if (error) {
                                        UIAlertView *alertView =
                                        [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                   message:error.localizedDescription
                                                                  delegate:self
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"Ok", nil];
                                        [alertView show];
                                    }else {
                                        NSLog(@"success");
                                        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"discountCode"];
                                        [[NSUserDefaults standardUserDefaults] synchronize];
                                        [[NSNotificationCenter defaultCenter] postNotificationName:@"PlaceOrderNotification" object:response];
                                    }
                                }];
    
}

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

- (void)textViewDidBeginEditing:(UITextView *)textView
{
    if ([textView.text isEqualToString:@"i.e. use scent-free detergent"]) {
        textView.text = @"";
        textView.textColor = [UIColor blackColor]; //optional
    }
    [textView becomeFirstResponder];
    
    activeView = textView;
}

- (void)textViewDidEndEditing:(UITextView *)textView
{
    if ([textView.text isEqualToString:@""]) {
        textView.text = @"i.e. use scent-free detergent";
        textView.textColor = [UIColor colorWithRed:199/255.0f green:199/255.0f blue:205/255.0f alpha:1];
    }
    [textView resignFirstResponder];
    
    activeView = nil;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }else if (textView.text.length > 149 && ![text isEqualToString:@""]) {
        return NO;
    }
    
    return YES;
}

-(void)keyboardWasShown:(NSNotification*)notification
{
    // keyboard frame is in window coordinates
    NSDictionary *userInfo = [notification userInfo];
    CGRect keyboardInfoFrame = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    // get the height of the keyboard by taking into account the orientation of the device too
    CGRect windowFrame = [self.view.window convertRect:self.view.frame fromView:self.view];
    CGRect keyboardFrame = CGRectIntersection (windowFrame, keyboardInfoFrame);
    CGRect coveredFrame = [self.view.window convertRect:keyboardFrame toView:self.view];
    
    // add the keyboard height to the content insets so that the scrollview can be scrolled
    UIEdgeInsets contentInsets = UIEdgeInsetsMake (0.0, 0.0, coveredFrame.size.height, 0.0);
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
    
    // make sure the scrollview content size width and height are greater than 0
    [scrollView setContentSize:CGSizeMake (scrollView.contentSize.width, scrollView.contentSize.height)];
    
    // scroll to the text view
    [scrollView scrollRectToVisible:activeView.frame animated:YES];
}

-(void)keyboardWillBeHidden:(NSNotification *)notification
{
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
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
