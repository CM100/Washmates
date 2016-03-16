//
//  AddressCell.h
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AddressCell : UITableViewCell

@property(nonatomic, retain) IBOutlet UILabel *lblTitle;
@property(nonatomic, retain) IBOutlet UILabel *lblDescription;
@property(nonatomic, retain) IBOutlet UIImageView *imgView;
@property(nonatomic, retain) IBOutlet UIImageView *imgViewArrow;

@end
