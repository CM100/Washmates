//
//  RatingViewController.h
//  Cleanium
//
//  Created by  DN-117 on 9/1/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TPFloatRatingView.h"
#import <Parse/Parse.h>

@protocol RatingDelegate
-(void)fromRating;
@end

@interface RatingViewController : UIViewController<TPFloatRatingViewDelegate>{

    IBOutlet TPFloatRatingView *ratingViewService;
    IBOutlet TPFloatRatingView *ratingViewPrice;
    IBOutlet TPFloatRatingView *ratingViewTiming;
}

@property(nonatomic, retain) PFObject *order;

@property (nonatomic, weak) id<RatingDelegate> delegate;

@end
