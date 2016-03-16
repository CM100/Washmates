//
//  TimeHelper.m
//  Cleanium
//
//  Created by marko on 8/2/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "TimeHelper.h"
#import "AppDelegate.h"

@implementation TimeHelper

+(NSMutableArray*)calculateTimeBlocksForDate:(NSDate*)date withReservedTimeBlocks:(NSArray*)reservedTimeBlocks withMaxDate:(NSDate*)maxDropOffDate{
    
//    NSDateFormatter *df = [[NSDateFormatter alloc] init];
//    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
//    [df setTimeZone:timeZone];
//    [df setDateFormat:@"yyyy-MM-dd"];
//    NSDate *date = [df dateFromString:dateString];
    
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [gregorianCalendar setTimeZone:timeZone];
    
    NSDate *maxDate;
    if ([self date:date isEqualToDate:maxDropOffDate]) {
        maxDate = maxDropOffDate;
    }else{
        NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:date];
        
        components.day = components.day + 1;
        components.hour = 0;
        components.minute = 0;
        components.second = 0;
        
        maxDate = [gregorianCalendar dateFromComponents:components];
    }
    
    NSMutableArray *timeBlocks = [[NSMutableArray alloc] init];

    while ([date compare:maxDate] == NSOrderedAscending) {
        NSDateComponents *componentsTimeBlock = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:date];
        
        componentsTimeBlock.minute = componentsTimeBlock.minute + appDelegate.driverInterval;
        
        NSDate *timeBlock = [gregorianCalendar dateFromComponents:componentsTimeBlock];
        
        NSComparisonResult  compareResult = [timeBlock compare:maxDate];
        if (compareResult == NSOrderedAscending || compareResult == NSOrderedSame) {
            bool isOk = NO;
            for (NSDictionary *timeDictionary in reservedTimeBlocks) {
                NSDate *reservedMinDate = [timeDictionary objectForKey:@"fromDate"];
                NSDate *reservedMaxDate = [timeDictionary objectForKey:@"toDate"];
                bool firstTimeInterval = [self date:date isBetweenDate:reservedMinDate andDate:reservedMaxDate];
                bool secondTimeInterval = [self date:timeBlock isBetweenDate:reservedMinDate andDate:reservedMaxDate];
                if (firstTimeInterval && secondTimeInterval) {
                    isOk = YES;
                }
            }
            if (!isOk) {
                [timeBlocks addObject:date];
            }
            
        }
        
        date = timeBlock;
    }
    
    return timeBlocks;
}

+ (BOOL)date:(NSDate*)date isBetweenDate:(NSDate*)beginDate andDate:(NSDate*)endDate
{
    if ([date compare:beginDate] == NSOrderedAscending)
        return NO;
    
    if ([date compare:endDate] == NSOrderedDescending)
        return NO;
    
    return YES;
}


+ (BOOL)date2:(NSDate*)date isBetweenDate:(NSDate*)beginDate andDate:(NSDate*)endDate{
    NSTimeInterval firstTimeInterVal = [beginDate timeIntervalSince1970];
    NSTimeInterval secondTimeInterVal = [endDate timeIntervalSince1970];
    NSTimeInterval nowTimeInterval = [date timeIntervalSince1970];
    
    if (nowTimeInterval >= firstTimeInterVal && nowTimeInterval <= secondTimeInterVal) {
        /**Between**/
        return YES;
    }else{
        /**Not Between**/
        return NO;
    }
}

+(NSString*)timeFromDate:(NSDate*)date{
    
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"HH:mm"];
    
    NSString *dateString = [df stringFromDate:date];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    [gregorianCalendar setTimeZone:timeZone];
    NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:date];
    components.minute = components.minute + appDelegate.driverInterval;
    
    NSDate *dateEnd = [gregorianCalendar dateFromComponents:components];
    NSString *dateStringEnd = [df stringFromDate:dateEnd];
    
    return [NSString stringWithFormat:@"%@ - %@", dateString, dateStringEnd];
}

+ (BOOL)date:(NSDate*)date isEqualToDate:(NSDate*)beginDate{
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-M-d"];
    
    NSString *dateString = [df stringFromDate:date];
    NSString *beginDateString = [df stringFromDate:beginDate];
    
    return [dateString isEqualToString:beginDateString];
    
}

+(bool)isTimeValid:(NSDate*)selectedTime withTimeArray:(NSArray*)timeArray withIndexFound:(NSNumber**)indexFound{
    
    if (!selectedTime) {
        return NO;
    }
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"HH:mm"];
    
    bool isValid = NO;
    NSString *selectedTimeString = [df stringFromDate:selectedTime];
    for (int i = 0; i < timeArray.count; i++) {
        NSDate *newTime = [timeArray objectAtIndex:i];
        NSString *newTimeString = [df stringFromDate:newTime];
        if ([selectedTimeString isEqualToString:newTimeString]) {
            isValid = YES;
            *indexFound = [NSNumber numberWithInt:i];
            break;
        }
    }

    return isValid;
}

@end
