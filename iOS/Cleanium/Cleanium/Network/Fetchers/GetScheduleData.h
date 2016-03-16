//
//  GetScheduleData.h
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GetScheduleData : NSObject

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
