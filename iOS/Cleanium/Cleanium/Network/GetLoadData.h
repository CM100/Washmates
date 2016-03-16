//
//  GetLoadData.h
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface GetLoadData : NSObject

+(void)getLoadData:(UIViewController*)viewController isFinished:(void (^)(NSNumber *))isFinished;

@end
