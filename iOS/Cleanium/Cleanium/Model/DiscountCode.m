//
//  DiscountCode.m
//  Cleanium
//
//  Created by marko on 9/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "DiscountCode.h"

@implementation DiscountCode

+(NSMutableDictionary*)toDictionaryFromResponse:(PFObject*)response{
    
    NSMutableDictionary *discountCodeDic = [[NSMutableDictionary alloc] init];
    [discountCodeDic setObject:response.objectId forKey:@"objectId"];
    NSNumber *amount = [response objectForKey:@"amount"];
    if (amount) {
        [discountCodeDic setObject:amount forKey:@"amount"];
    }
    NSString *code = [response objectForKey:@"code"];
    if (code) {
        [discountCodeDic setObject:code forKey:@"code"];
    }
    NSNumber *isPercentage = [response objectForKey:@"isPercentage"];
    if (isPercentage) {
        [discountCodeDic setObject:isPercentage forKey:@"isPercentage"];
    }
    
    return discountCodeDic;
}

@end
