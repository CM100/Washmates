//
//  OrderHistoryViewController.m
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "OrderHistoryViewController.h"
#import "OrderHistoryCell.h"
#import <Parse/Parse.h>
#import "CustomPopUp.h"
#import "Order.h"

@interface OrderHistoryViewController ()

@end

@implementation OrderHistoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"ORDER HISTORY";
    
    appDelegate = appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"dd.MM.yyyy"];
    
    tableData = [[NSMutableArray alloc] init];
    for (PFObject *order in appDelegate.userOrders) {
        NSDictionary *statusDic = [self getStatusDictionary:[order objectForKey:@"status"]];
        if ([[statusDic objectForKey:@"showOnHistory"] boolValue]) {
            Order *orderObject = [[Order alloc] init];
            orderObject.orderStatusText = [statusDic objectForKey:@"text"];
            
            UIColor *color = [self colorFromHexString:[statusDic objectForKey:@"color"]];
            if (!color) {
                color = [UIColor blackColor];
            }
            orderObject.orderColor = color;
            
            PFObject *object = [order objectForKey:@"pickUpSchedule"];
            NSDate *fromDate = [object objectForKey:@"fromDate"];
            orderObject.orderPickupDate = [df stringFromDate:fromDate];
            
            object = [order objectForKey:@"deliverySchedule"];
            NSDate *toDate = [object objectForKey:@"toDate"];
            orderObject.orderDropOffDate = [df stringFromDate:toDate];
            
            object = [order objectForKey:@"paymentReceipt"];
            NSNumber *totalAmount = [object objectForKey:@"total"];
            orderObject.orderTotalAmount = [NSString stringWithFormat:@"$%.1f", [totalAmount floatValue]];
            orderObject.orderTotalAmountNumber = totalAmount;
            
            NSArray *services = [object objectForKey:@"services"];
            orderObject.orderServices = services;
            
            [tableData addObject:orderObject];
        }
    }
}

//-(void)viewWillAppear:(BOOL)animated{
//    
//    [super viewWillAppear:animated];
//    
//    tableData = appDelegate.userOrders;
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

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return tableData.count;
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    NSString *CellIdentifier = @"OrderHistoryCell";
    
    
    OrderHistoryCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    }
    
    Order *order = [tableData objectAtIndex:indexPath.row];

    cell.lblPickupDate.text = order.orderPickupDate;
    
    cell.lblDropOffDate.text = order.orderDropOffDate;
    
    cell.lblCost.text = order.orderTotalAmount;
    
    cell.lblStatus.text = order.orderStatusText;
    
    UIColor *color = order.orderColor;
    [cell.lblStatus setTextColor:color];
    
    [cell.contentView setBackgroundColor:color];
    
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    Order *order = [tableData objectAtIndex:indexPath.row];
    if (order.orderServices && order.orderTotalAmountNumber) {
        [CustomPopUp showCostBreakdownPopUp:order.orderServices withTotalAmount:order.orderTotalAmountNumber];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"This order doesn't has a bill or is not in valid format." delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
    
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 108;
}

-(NSDictionary*)getStatusDictionary:(NSString*)status{
    
    for (NSDictionary *statusDic in appDelegate.orderStatuses) {
        if ([[statusDic objectForKey:@"name"] isEqualToString:status]) {
            return statusDic;
        }
    }
    
    return nil;
}

-(UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1]; // bypass '#' character
    [scanner scanHexInt:&rgbValue];
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
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
