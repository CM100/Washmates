//
//  DateHelper.h
//  Cleanium
//
//  Created by  DN-117 on 7/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CalculatedDate.h"

@interface DateHelper : NSObject

+(CalculatedDate*)calculateDates:(NSDate*)minDate andMaxDate:(NSDate*)maxDate;
+(NSDictionary*)stringWithDateFormat:(NSString*)dateString;
+(NSMutableDictionary*)calculateDropOffDate:(NSDate*)minDate andMaxDate:(NSDate*)maxDate isOtherService:(bool)isOtherService;
+(NSString*)toDateFromDate:(NSDate*)date;
+(NSString*)toDateStringFromDate:(NSDate*)date;
+(NSDictionary*)toMM_ddStringFromDate:(NSDate*)date;
+(bool)isSelectedDropOffDateValid:(NSDate*)selectedDropOffDate withSelectedPickUpDate:(NSDate*)selectedPickUpDate isOtherService:(bool)isOtherService;
+(NSString*)toYYYY_M_D_DateStringFromDate:(NSDate*)date;
+(NSDate*)dateTo7DayAhead:(NSDate*)date;
+(NSDate*)getMinPickUpDate:(NSDate*)date;

@end
