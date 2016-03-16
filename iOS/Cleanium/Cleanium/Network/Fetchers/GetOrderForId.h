//
//  GetOrderForId.h
//  Cleanium
//
//  Created by  DN-117 on 9/8/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GetOrderForId : NSObject

@property(nonatomic, retain) NSString *orderId;

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
