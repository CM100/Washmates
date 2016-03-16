//
//  TutorialViewController.h
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Tutorial1View.h"
#import "Tutorial2View.h"
#import "Tutorial3View.h"

@protocol TutorialViewControllerDelegate
-(void)okTutorialViewController;
@end

@interface TutorialViewController : UIViewController<Tutorial1Delegate, Tutorial2Delegate, Tutorial3Delegate>{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet UIPageControl *pageControl;
    
    NSMutableArray *tutorialViews;
}

@property (nonatomic, weak) id<TutorialViewControllerDelegate> delegate;

@end
