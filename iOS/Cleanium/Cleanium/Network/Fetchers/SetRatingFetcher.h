//
//  SetRatingFetcher.h
//  Cleanium
//
//  Created by  DN-117 on 9/2/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Parse/Parse.h>

@interface SetRatingFetcher : NSObject

@property(nonatomic, retain) PFObject *rating;
@property(nonatomic, retain) UIView *view;

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
