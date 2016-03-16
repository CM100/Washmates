//
//  Order.h
//  Cleanium
//
//  Created by  DN-117 on 9/10/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface Order : NSObject

@property(nonatomic, retain) NSString *orderPickupDate;
@property(nonatomic, retain) NSString *orderDropOffDate;
@property(nonatomic, retain) NSString *orderTotalAmount;
@property(nonatomic, retain) NSString *orderStatusText;
@property(nonatomic, retain) UIColor *orderColor;
@property(nonatomic, retain) NSArray *orderServices;
@property(nonatomic, retain) NSNumber *orderTotalAmountNumber;

@end
