//
//  ScheduleData.h
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ReservedScheduleData.h"

@interface ScheduleData : NSObject

@property(nonatomic, retain) NSDate *minPickUpDate;
@property(nonatomic, retain) NSDate *maxDeliveryDate;
@property(nonatomic, retain) NSMutableArray *reserved;
@property(nonatomic, retain) NSMutableDictionary *reservedDictionary;

-(id)initWithJson:(NSDictionary*)json;

@end
