//
//  MenuViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/23/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "MenuViewController.h"
#import "SlideNavigationController.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"
#import "MenuCell.h"
#import "OrderHistoryViewController.h"
#import "SettingsViewController.h"
#import "RatingViewController.h"
#import "PricingViewController.h"
#import "CouponViewController.h"

@interface MenuViewController ()

@end

@implementation MenuViewController

-(id)initWithCoder:(NSCoder *)aDecoder{
    self = [super initWithCoder:aDecoder];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handlePushNotification:)
                                                     name:@"PushNotification"
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handlePushNotification:)
                                                     name:@"PushNotificationBackground"
                                                   object:nil];
    }
    
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)handlePushNotification:(NSNotification *)notification
{
    
    if ([[notification name] isEqualToString:@"PushNotification"]){
        NSLog (@"Successfully received the test notification!");
        
        NSDictionary *userInfo = notification.userInfo;
        [self callRefreshOrder:[userInfo objectForKey:@"orderId"] fromBackground:NO];
        
    }else if ([[notification name] isEqualToString:@"PushNotificationBackground"]){
        NSDictionary *userInfo = notification.userInfo;
        [self callRefreshOrder:[userInfo objectForKey:@"orderId"] fromBackground:YES];
        
    }
}

-(void)callRefreshOrder:(NSString*)orderId fromBackground:(bool)fromBackground{
    if (washViewController) {
        if (fromBackground) {
            UIViewController *viewController = [[[SlideNavigationController sharedInstance] viewControllers] firstObject];
            if (![viewController isKindOfClass:[WashStatusViewController class]]) {
                [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:washViewController withSlideOutAnimation:NO andCompletion:nil];
            }
        }
        [washViewController refreshOrder:orderId];
    }else{
        UIViewController *viewController = [[[SlideNavigationController sharedInstance] viewControllers] firstObject];
        if ([viewController isKindOfClass:[WashStatusViewController class]]) {
            washViewController = (WashStatusViewController*)viewController;
            [washViewController refreshOrder:orderId];
        }
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    menuData = [[NSMutableArray alloc] init];
    
    NSMutableDictionary *dicHome = [[NSMutableDictionary alloc] init];
    [dicHome setObject:@"Wash status" forKey:@"title"];
    [dicHome setObject:[UIImage imageNamed:@"home-white.png"] forKey:@"image"];
    [dicHome setObject:[UIImage imageNamed:@"home-blue.png"] forKey:@"imageSel"];
    [dicHome setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicHome];
    NSMutableDictionary *dicSchadule = [[NSMutableDictionary alloc] init];
    [dicSchadule setObject:@"Schedule a pickup" forKey:@"title"];
    [dicSchadule setObject:[UIImage imageNamed:@"Schadule-a-pickup---white.png"] forKey:@"image"];
    [dicSchadule setObject:[UIImage imageNamed:@"Schadule-a-pickup---blue.png"] forKey:@"imageSel"];
    [dicSchadule setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicSchadule];
    NSMutableDictionary *dicPricing = [[NSMutableDictionary alloc] init];
    [dicPricing setObject:@"Pricing" forKey:@"title"];
    [dicPricing setObject:[UIImage imageNamed:@"pricing-white.png"] forKey:@"image"];
    [dicPricing setObject:[UIImage imageNamed:@"pricing-blue.png"] forKey:@"imageSel"];
    [dicPricing setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicPricing];
    NSMutableDictionary *dicOrder = [[NSMutableDictionary alloc] init];
    [dicOrder setObject:@"Order History" forKey:@"title"];
    [dicOrder setObject:[UIImage imageNamed:@"order-history-white.png"] forKey:@"image"];
    [dicOrder setObject:[UIImage imageNamed:@"order-history-blue.png"] forKey:@"imageSel"];
    [dicOrder setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicOrder];
    NSMutableDictionary *dicSupport = [[NSMutableDictionary alloc] init];
    [dicSupport setObject:@"Support" forKey:@"title"];
    [dicSupport setObject:[UIImage imageNamed:@"contact-white.png"] forKey:@"image"];
    [dicSupport setObject:[UIImage imageNamed:@"contact-blue.png"] forKey:@"imageSel"];
    [dicSupport setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicSupport];
    NSMutableDictionary *dicSettings = [[NSMutableDictionary alloc] init];
    [dicSettings setObject:@"Settings" forKey:@"title"];
    [dicSettings setObject:[UIImage imageNamed:@"settings-white.png"] forKey:@"image"];
    [dicSettings setObject:[UIImage imageNamed:@"settings-blue.png"] forKey:@"imageSel"];
    [dicSettings setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicSettings];
    NSMutableDictionary *dicCoupons = [[NSMutableDictionary alloc] init];
    [dicCoupons setObject:@"Coupons" forKey:@"title"];
    [dicCoupons setObject:[UIImage imageNamed:@"coupons-white.png"] forKey:@"image"];
    [dicCoupons setObject:[UIImage imageNamed:@"coupons-blue.png"] forKey:@"imageSel"];
    [dicCoupons setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicCoupons];
    NSMutableDictionary *dicLogout = [[NSMutableDictionary alloc] init];
    [dicLogout setObject:@"Logout" forKey:@"title"];
    [dicLogout setObject:[UIImage imageNamed:@"logout-white.png"] forKey:@"image"];
    [dicLogout setObject:[UIImage imageNamed:@"logout-blue.png"] forKey:@"imageSel"];
    [dicLogout setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [menuData addObject:dicLogout];
    
    UIViewController *viewController = [[[SlideNavigationController sharedInstance] viewControllers] firstObject];
    if ([viewController isKindOfClass:[WashStatusViewController class]] && !washViewController) {
        washViewController = (WashStatusViewController*)viewController;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)logOut{
//    CATransition *animation=[CATransition animation];
//    animation.delegate=self;
//    animation.duration=0.3;
//    animation.type=kCATransitionReveal;
//    animation.subtype=kCATransitionFromLeft;
//    [self.view.window.layer addAnimation:animation forKey:kCATransition];

    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Logout";
    
    [PFUser logOutInBackgroundWithBlock:^(NSError *error)
     {
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
             PFInstallation *currentInstallation = [PFInstallation currentInstallation];
             currentInstallation.channels = [NSArray array];
             [currentInstallation saveEventually];
             [self.delegate fromLogout];
             [[SlideNavigationController sharedInstance] dismissViewControllerAnimated:NO completion:nil];
         }
     }];
    
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return menuData.count;
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    

    NSString *CellIdentifier = @"MenuCell";
    
    
    MenuCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    }
    
    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
    
    if ([[dic objectForKey:@"isSelected"] boolValue]) {
        cell.imgView.image = [dic objectForKey:@"imageSel"];
        cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
        [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    }else{
        cell.imgView.image = [dic objectForKey:@"image"];
        cell.imgViewArrow.image = [UIImage imageNamed:@"whiteRightArow.png"];
        [cell.lblTitle setTextColor:[UIColor whiteColor]];
    }
    
    cell.lblTitle.text = [dic objectForKey:@"title"];
    
    return cell;
    
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    [cell setBackgroundColor:[UIColor clearColor]];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSLog(@"klik");
//    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    NSMutableDictionary *dic = [menuData objectAtIndex:indexPath.row];
    cell.imgView.image = [dic objectForKey:@"imageSel"];
    cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
    [dic setObject:[NSNumber numberWithBool:YES] forKey:@"isSelected"];
    [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    
    switch (indexPath.row) {
        case 0:{
            if (!washViewController) {
                washViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"WashStatusViewController"];
            }
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:washViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }
        case 1:{
            if (!scheduleViewController) {
                scheduleViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"ScheduleViewController"];
            }
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:scheduleViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }case 2:{
            PricingViewController *pricingViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"PricingViewController"];
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:pricingViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }case 3:{
            OrderHistoryViewController *orderHistoryViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"OrderHistoryViewController"];
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:orderHistoryViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }case 5:{
            SettingsViewController *settingsViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"SettingsViewController"];
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:settingsViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }case 6:{
            CouponViewController *couponViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"CouponViewController"];
            [[SlideNavigationController sharedInstance] popAllAndSwitchToViewController:couponViewController withSlideOutAnimation:NO andCompletion:nil];
            break;
        }
        case 7:{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Are you sure that you want to log out?" message:nil delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
            [alert show];
            break;
        }
            
        default:
            break;
    }
    
}

-(void) tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    NSMutableDictionary *dic = [menuData objectAtIndex:indexPath.row];
    cell.imgView.image = [dic objectForKey:@"image"];
    cell.imgViewArrow.image = [UIImage imageNamed:@"whiteRightArow.png"];
    [dic setObject:[NSNumber numberWithBool:NO] forKey:@"isSelected"];
    [cell.lblTitle setTextColor:[UIColor whiteColor]];
    [self setCellColor:[UIColor clearColor] ForCell:cell];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 50;
}

- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
    cell.imgView.image = [dic objectForKey:@"imageSel"];
    cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
    [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    [self setCellColor:[UIColor whiteColor] ForCell:cell];
}

- (void)tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (![cell isSelected]) {
        [self setCellColor:[UIColor clearColor] ForCell:cell];
        NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
        cell.imgView.image = [dic objectForKey:@"image"];
        cell.imgViewArrow.image = [UIImage imageNamed:@"whiteRightArow.png"];
        [cell.lblTitle setTextColor:[UIColor whiteColor]];
    }
    
    
//    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

- (void)setCellColor:(UIColor *)color ForCell:(UITableViewCell *)cell {
    cell.contentView.backgroundColor = color;
    cell.backgroundColor = color;
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 1) {
        [self logOut];
    }
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
