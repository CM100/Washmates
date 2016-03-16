//
//  ScheduleData.m
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "ScheduleData.h"

@implementation ScheduleData

@synthesize minPickUpDate, maxDeliveryDate, reserved, reservedDictionary;

-(id)initWithJson:(NSDictionary*)json
{
    self = [super init];
    if(self) {
        [self fromJson:json];
    }
    return self;
}

-(void)fromJson:(NSDictionary*)json{
    
    id jsonField = [json objectForKey:@"minPickUpDate"];
    if (!jsonField)
        minPickUpDate = nil;
    else if ([jsonField isKindOfClass: [NSDate class]])
        minPickUpDate = [self dateWithJSONString:[NSString stringWithFormat:@"%@", jsonField]];
    else if (jsonField == [NSNull null])
        minPickUpDate = nil;
    
    jsonField = [json objectForKey:@"maxDeliveryDate"];
    if (!jsonField)
        maxDeliveryDate = nil;
    else if ([jsonField isKindOfClass: [NSDate class]])
        maxDeliveryDate = [self dateWithJSONString:[NSString stringWithFormat:@"%@", jsonField]];
    else if (jsonField == [NSNull null])
        maxDeliveryDate = nil;
    
    reservedDictionary = [[NSMutableDictionary alloc] init];
    reserved =[[NSMutableArray alloc] init];
    NSArray *reservedArray = [json objectForKey:@"reserved"];
    for (NSDictionary *dic in reservedArray) {
        ReservedScheduleData *reservedScheduleData = [[ReservedScheduleData alloc] initWithJson:dic];
        [reserved addObject:reservedScheduleData];
        
        NSString *dayKey = [self dateToString:reservedScheduleData.reservedFromDate];
        
        for (NSString *postCode in reservedScheduleData.reservedPostCodes) {
            if ([reservedDictionary objectForKey:postCode] == nil) {
                NSMutableDictionary *dayDictionary = [[NSMutableDictionary alloc] init];
                NSMutableArray *timeBlocks = [[NSMutableArray alloc] init];
                NSMutableDictionary *timeDictionary = [[NSMutableDictionary alloc] init];
                [timeDictionary setObject:reservedScheduleData.reservedFromDate forKey:@"fromDate"];
                [timeDictionary setObject:reservedScheduleData.reservedToDate forKey:@"toDate"];
                [timeBlocks addObject:timeDictionary];
                [dayDictionary setObject:timeBlocks forKey:dayKey];
                [reservedDictionary setObject:dayDictionary forKey:postCode];
            }else{
                NSMutableDictionary *dayDictionary = [reservedDictionary objectForKey:postCode];
                if ([dayDictionary objectForKey:dayKey] == nil) {
                    NSMutableArray *timeBlocks = [[NSMutableArray alloc] init];
                    NSMutableDictionary *timeDictionary = [[NSMutableDictionary alloc] init];
                    [timeDictionary setObject:reservedScheduleData.reservedFromDate forKey:@"fromDate"];
                    [timeDictionary setObject:reservedScheduleData.reservedToDate forKey:@"toDate"];
                    [timeBlocks addObject:timeDictionary];
                    [dayDictionary setObject:timeBlocks forKey:dayKey];
                    [reservedDictionary setObject:dayDictionary forKey:postCode];
                }else{
                    NSMutableArray *timeBlocks = [dayDictionary objectForKey:dayKey];
                    NSMutableDictionary *timeDictionary = [[NSMutableDictionary alloc] init];
                    [timeDictionary setObject:reservedScheduleData.reservedFromDate forKey:@"fromDate"];
                    [timeDictionary setObject:reservedScheduleData.reservedToDate forKey:@"toDate"];
                    [timeBlocks addObject:timeDictionary];
                    [dayDictionary setObject:timeBlocks forKey:dayKey];
                    [reservedDictionary setObject:dayDictionary forKey:postCode];
                }
                
            }
        }
        
    }
    
}

- (NSDate*)dateWithJSONString:(NSString*)dateStr
{
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"yyyy-MM-dd HH:mm:ssZ"];
    NSDate *date = [dateFormat dateFromString:dateStr];

    return date;
}

- (NSString*)dateToString:(NSDate*)date
{
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
    [dateFormat setTimeZone:timeZone];
    [dateFormat setDateFormat:@"yyyy-M-d"];
    NSString *dateString = [dateFormat stringFromDate:date];
    
    return dateString;
}

@end
