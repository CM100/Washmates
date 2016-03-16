//
//  PricingViewController.m
//  Cleanium
//
//  Created by  DN-117 on 9/10/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "PricingViewController.h"

@interface PricingViewController ()

@end

@implementation PricingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"PRICING";
    
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setObject:@"Bag of laundry (max 10 kg)" forKey:@"title"];
    [dic setObject:@"$24 per bag" forKey:@"price"];
    [breakdownView1 setServicePricing:dic];
    
    [dic setObject:@"Iron shirts" forKey:@"title"];
    [dic setObject:@"$3.50 each" forKey:@"price"];
    [breakdownView2 setServicePricing:dic];
    
    [dic setObject:@"Dry cleaning standard items\nThis includes: Trousers, Slacks, Jackets, Vests, Skirts, Jumpers, Ties, Blouses" forKey:@"title"];
    [dic setObject:@"$10 each" forKey:@"price"];
    [breakdownView3 setServicePricing:dic];
    
    [dic setObject:@"Dry cleaning not standard items as per a list.\nThis includes: Full length coats, Dresses, Doonas, Bedspreads" forKey:@"title"];
    [dic setObject:@"" forKey:@"price"];
    [breakdownView4 setServicePricing:dic];
    
    [dic setObject:@"Pickup and Delivery: Free for orders over $20" forKey:@"title"];
    [dic setObject:@"Free" forKey:@"price"];
    [breakdownView5 setServicePricing:dic];
    
    [dic setObject:@"Pickup and Delivery: Free for orders below $20" forKey:@"title"];
    [dic setObject:@"$7" forKey:@"price"];
    [breakdownView6 setServicePricing:dic];
    
    [dic setObject:@"No show fee" forKey:@"title"];
    [dic setObject:@"$7" forKey:@"price"];
    [breakdownView7 setServicePricing:dic];
    [breakdownView7.lineView setHidden:YES];
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
