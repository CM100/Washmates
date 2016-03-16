//
//  GetOrdersFetcher.h
//  Cleanium
//
//  Created by  DN-117 on 8/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GetOrdersFetcher : NSObject

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
