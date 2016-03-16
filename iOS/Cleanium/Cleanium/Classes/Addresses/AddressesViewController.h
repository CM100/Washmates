//
//  AddressesViewController.h
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"

@interface AddressesViewController : UIViewController{
    
    IBOutlet UITableView *mainTable;
    IBOutlet NSLayoutConstraint *heightConstraint;
    IBOutlet UIButton *btnAddAddress;
    IBOutlet UIView *lineView;
    
    NSMutableArray *tableData;
    UIColor *cellColor;
    
    AppDelegate *appDelegate;
}

@property(nonatomic, assign) bool isAddress;

@end
