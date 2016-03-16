//
//  ReservedScheduleData.m
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "ReservedScheduleData.h"

@implementation ReservedScheduleData

@synthesize reservedFromDate, reservedToDate, reservedPostCodes;

-(id)initWithJson:(NSDictionary*)json
{
    self = [super init];
    if(self) {
        [self fromJson:json];
    }
    return self;
}

-(void)fromJson:(NSDictionary*)json{
    
    id jsonField = [json objectForKey:@"fromDate"];
    if (!jsonField)
        reservedFromDate = nil;
    else if ([jsonField isKindOfClass: [NSDate class]])
        reservedFromDate = [self dateWithJSONString:[NSString stringWithFormat:@"%@", jsonField]];
    else if (jsonField == [NSNull null])
        reservedFromDate = nil;
    
    jsonField = [json objectForKey:@"toDate"];
    if (!jsonField)
        reservedToDate = nil;
    else if ([jsonField isKindOfClass: [NSDate class]])
        reservedToDate = [self dateWithJSONString:[NSString stringWithFormat:@"%@", jsonField]];
    else if (jsonField == [NSNull null])
        reservedToDate = nil;
    
    reservedPostCodes = [json objectForKey:@"postCode"];
    
}

- (NSDate*)dateWithJSONString:(NSString*)dateStr
{
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"yyyy-MM-dd HH:mm:ssZ"];
    NSDate *date = [dateFormat dateFromString:dateStr];
    
    return date;
}

@end
