//
//  OrderHistoryViewController.h
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"

@interface OrderHistoryViewController : UIViewController{
    
    IBOutlet UITableView *mainTable;
    
    AppDelegate *appDelegate;
    NSMutableArray *tableData;
    
    NSDateFormatter *df;
}

@end
