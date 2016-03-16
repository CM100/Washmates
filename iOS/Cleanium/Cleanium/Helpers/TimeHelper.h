//
//  TimeHelper.h
//  Cleanium
//
//  Created by marko on 8/2/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TimeHelper : NSObject

+(NSMutableArray*)calculateTimeBlocksForDate:(NSDate*)date withReservedTimeBlocks:(NSArray*)reservedTimeBlocks withMaxDate:(NSDate*)maxDropOffDate;
+(NSString*)timeFromDate:(NSDate*)date;
+(bool)isTimeValid:(NSDate*)selectedTime withTimeArray:(NSArray*)timeArray withIndexFound:(NSNumber**)indexFound;

@end
