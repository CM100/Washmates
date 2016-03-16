//
//  AddressCell.m
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "AddressCell.h"

@implementation AddressCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
    UIView * selectedBackgroundView = [[UIView alloc] init];
    [selectedBackgroundView setBackgroundColor:[UIColor whiteColor]];
    [self setSelectedBackgroundView:selectedBackgroundView];
}

@end
