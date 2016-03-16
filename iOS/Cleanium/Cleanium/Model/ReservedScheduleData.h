//
//  ReservedScheduleData.h
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ReservedScheduleData : NSObject

@property(nonatomic, retain) NSDate *reservedFromDate;
@property(nonatomic, retain) NSDate *reservedToDate;
@property(nonatomic, retain) NSMutableArray *reservedPostCodes;

-(id)initWithJson:(NSDictionary*)json;

@end
