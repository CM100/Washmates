//
//  CustomPopUp.h
//  Cleanium
//
//  Created by  DN-117 on 8/6/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PaymentInfoPopUp.h"
#import "CostBreakdownPopUp.h"

@interface CustomPopUp : NSObject

+(void)showPaymenInfo:(id <PaymentInfoPopUpDelegate>)delegate;
+(void)showCostBreakdownPopUp:(NSArray*)services withTotalAmount:(NSNumber*)totalAmount;

@end
