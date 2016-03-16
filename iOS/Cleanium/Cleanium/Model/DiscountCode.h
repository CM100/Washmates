//
//  DiscountCode.h
//  Cleanium
//
//  Created by marko on 9/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Parse/Parse.h>

@interface DiscountCode : NSObject

+(NSMutableDictionary*)toDictionaryFromResponse:(PFObject*)response;

@end
