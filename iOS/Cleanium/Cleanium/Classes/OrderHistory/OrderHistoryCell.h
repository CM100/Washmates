//
//  OrderHistoryCell.h
//  Cleanium
//
//  Created by  DN-117 on 9/9/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface OrderHistoryCell : UITableViewCell

@property(nonatomic, retain) IBOutlet UILabel *lblPickupDate;
@property(nonatomic, retain) IBOutlet UILabel *lblDropOffDate;
@property(nonatomic, retain) IBOutlet UILabel *lblCost;
@property(nonatomic, retain) IBOutlet UILabel *lblStatus;

@end
