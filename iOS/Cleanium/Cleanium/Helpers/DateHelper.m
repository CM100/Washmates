//
//  DateHelper.m
//  Cleanium
//
//  Created by  DN-117 on 7/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "DateHelper.h"
#import "AppDelegate.h"

@implementation DateHelper

+(CalculatedDate*)calculateDates:(NSDate*)minDate andMaxDate:(NSDate*)maxDate{
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-MM-dd HH:mm:ssZ"];
//    NSString *minDateString = [df stringFromDate:minDate];
//    NSString *maxDateString = [df stringFromDate:maxDate];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    [gregorianCalendar setTimeZone:timeZone];
    NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit
                                                         fromDate:minDate];
    NSString *minYear = [NSString stringWithFormat:@"%li", (long)components.year];
//    NSString *minMonth = [NSString stringWithFormat:@"%i", components.month];
//    NSInteger minDay = components.day;
    
    NSDateComponents *componentsMax = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit
                                                        fromDate:maxDate];
    NSString *maxYear = [NSString stringWithFormat:@"%li", (long)componentsMax.year];
//    NSString *maxMonth = [NSString stringWithFormat:@"%i", componentsMax.month];
    NSInteger maxDay = componentsMax.day;
    
    int orderNumber = 1;
    NSInteger currentYear = components.year;
    NSInteger currentMonth = components.month;
    NSInteger currentDay = components.day;
    NSMutableDictionary *dateDictionary = [[NSMutableDictionary alloc] init];
    while ([minDate compare:maxDate] == NSOrderedAscending) {
        NSMutableArray *dayForMonth = [[NSMutableArray alloc] init];
        NSInteger maxDayOfMonth;
        if (currentMonth == componentsMax.month){
            maxDayOfMonth = maxDay;
        }else{
            maxDayOfMonth = [self daysForMonth:currentMonth];
        }
        
        if (currentMonth > 12) {
            currentYear++;
            currentMonth = 1;
        }
        
        for (NSInteger i = currentDay; i <= maxDayOfMonth; i++) {
            [dayForMonth addObject:[NSString stringWithFormat:@"%li", (long)i]];
        }
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        [dic setObject:[NSString stringWithFormat:@"%li", (long)currentYear] forKey:@"year"];
        [dic setObject:dayForMonth forKey:@"days"];
        [dic setObject:[NSString stringWithFormat:@"%i", orderNumber] forKey:@"orderNumber"];
        [dateDictionary setObject:dic forKey:[NSString stringWithFormat:@"%li", (long)currentMonth]];
        
        currentMonth++;
        currentDay = 1;
        orderNumber++;
        
        components.day = componentsMax.day;
        components.month = currentMonth;
        components.year = currentYear;
        minDate = [gregorianCalendar dateFromComponents:components];
    }

    CalculatedDate *calcDate = [[CalculatedDate alloc] init];
    calcDate.calcMinYear = minYear;
    calcDate.calcMaxYear = maxYear;
    calcDate.calcDateDictionary = dateDictionary;
    
    return calcDate;
}

+(NSInteger)daysForMonth:(NSInteger)month{
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    
    NSDateComponents *componentsMonth = [[NSDateComponents alloc] init];
    [componentsMonth setMonth:month];
    NSRange range = [gregorianCalendar rangeOfUnit:NSCalendarUnitDay
                                            inUnit:NSCalendarUnitMonth
                                           forDate:[gregorianCalendar dateFromComponents:componentsMonth]];
    NSLog(@"%lu", (unsigned long)range.length);
    
    return range.length;
}

+(NSDictionary*)stringWithDateFormat:(NSString*)dateString{
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    [df setDateFormat:@"yyyy-MM-dd"];
    NSDate *date = [df dateFromString:dateString];
    
    [df setDateFormat:@"MM/dd"];
    NSString *stringFromDate = [df stringFromDate:date];
    
    [df setDateFormat:@"EEEE"];
    NSString *dayName = [df stringFromDate:date];
    
    NSDictionary *dateDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:stringFromDate, @"formatedDate", dayName, @"dayName", nil];
    
    return dateDictionary;
}

+(NSMutableDictionary*)calculateDropOffDate:(NSDate*)minDate andMaxDate:(NSDate*)maxDate isOtherService:(bool)isOtherService{

//    NSDateFormatter *df = [[NSDateFormatter alloc] init];
//    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
//    [df setTimeZone:timeZone];
//    [df setDateFormat:@"yyyy-MM-dd"];
//    NSDate *minDate = [df dateFromString:minDateString];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [gregorianCalendar setTimeZone:timeZone];
    NSDateComponents *componentsMin = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                        fromDate:minDate];
    
    if (isOtherService) {
        componentsMin.hour = componentsMin.hour + 24;
    }else{
        componentsMin.hour = componentsMin.hour + 6;
    }
    
    minDate = [gregorianCalendar dateFromComponents:componentsMin];
    
    NSDateComponents *componentsMax = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                           fromDate:minDate];
    componentsMax.day = componentsMax.day + 7;
    
    NSDate *pomMaxDate = [gregorianCalendar dateFromComponents:componentsMax];

    while ([pomMaxDate compare:maxDate] == NSOrderedDescending) {
        componentsMax .day= componentsMax.day - 1;
        pomMaxDate = [gregorianCalendar dateFromComponents:componentsMax];
    }
    
    if ([pomMaxDate compare:minDate] == NSOrderedSame && [pomMaxDate compare:maxDate] == NSOrderedAscending) {
        pomMaxDate = maxDate;
    }
    
    NSMutableDictionary *dateDictionary = [[NSMutableDictionary alloc] init];
    [dateDictionary setObject:minDate forKey:@"minDate"];
    [dateDictionary setObject:pomMaxDate forKey:@"maxDate"];
    
    return dateDictionary;
}

+(NSString*)toDateFromDate:(NSDate*)date{
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [gregorianCalendar setTimeZone:timeZone];
    NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:date];
    components.minute = components.minute + appDelegate.driverInterval;
    
    NSDate *toDate = [gregorianCalendar dateFromComponents:components];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-MM-dd HH:mm:ssZ"];
    
    return [df stringFromDate:toDate];
}

+(NSString*)toDateStringFromDate:(NSDate*)date{

    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-MM-dd HH:mm:ssZ"];
    
    return [df stringFromDate:date];
}

+(NSDictionary*)toMM_ddStringFromDate:(NSDate*)date{
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"MM/dd"];
    NSString *stringFromDate = [df stringFromDate:date];
    
    [df setDateFormat:@"EEEE"];
    NSString *dayName = [df stringFromDate:date];
    
    NSDictionary *dateDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:stringFromDate, @"formatedDate", dayName, @"dayName", nil];
    
    return dateDictionary;
}

+(bool)isSelectedDropOffDateValid:(NSDate*)selectedDropOffDate withSelectedPickUpDate:(NSDate*)selectedPickUpDate isOtherService:(bool)isOtherService{
    
    if (!selectedDropOffDate) {
        return NO;
    }
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [gregorianCalendar setTimeZone:timeZone];
    
    NSDateComponents *componentsMax = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                           fromDate:selectedPickUpDate];
    componentsMax.day = componentsMax.day + 7;
    
    NSDate *pomMaxDate = [gregorianCalendar dateFromComponents:componentsMax];
    
    bool isValid = NO;
    if ([selectedDropOffDate compare:pomMaxDate] == NSOrderedAscending) {
        isValid = YES;
    }
    
    NSDateComponents *componentsMin = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                           fromDate:selectedPickUpDate];
    
    if (isOtherService) {
        componentsMin.hour = componentsMin.hour + 24;
    }else{
        componentsMin.hour = componentsMin.hour + 6;
    }
    NSDate *minDate = [gregorianCalendar dateFromComponents:componentsMin];
    
    if ([selectedDropOffDate compare:minDate] == NSOrderedAscending) {
        isValid = NO;
    }
    
    return isValid;
}

+(NSString*)toYYYY_M_D_DateStringFromDate:(NSDate*)date{
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [df setTimeZone:timeZone];
    [df setDateFormat:@"yyyy-M-d"];
    
    return [df stringFromDate:date];
}

+(NSDate*)dateTo7DayAhead:(NSDate*)date{
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [gregorianCalendar setTimeZone:timeZone];
    
    NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                           fromDate:date];
    components.day = components.day + 7;
    
    NSDate *date7DaysAhead = [gregorianCalendar dateFromComponents:components];
    
    return date7DaysAhead;
}

+(NSDate*)getMinPickUpDate:(NSDate*)date{

    NSDate *pomCurrentDate = [NSDate date];
    NSTimeZone *tz = [NSTimeZone localTimeZone];
    NSInteger seconds = [tz secondsFromGMTForDate:pomCurrentDate];
    NSDate *currentDate = [NSDate dateWithTimeInterval: seconds sinceDate:pomCurrentDate];
    
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit
                                                        fromDate:currentDate];
    components.hour = components.hour + 1;
    if (components.minute > 0 && components.minute < 30) {
        components.minute = 30;
    }else if (components.minute > 30){
        components.minute = 60;
    }
    
    NSDate *currentDatePlus1 = [gregorianCalendar dateFromComponents:components];
    
    if ([date compare:currentDatePlus1] == NSOrderedAscending) {
        date = currentDatePlus1;
    }
    
    return date;
}

@end
