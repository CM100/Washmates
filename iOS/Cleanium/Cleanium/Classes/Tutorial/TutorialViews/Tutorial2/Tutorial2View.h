//
//  Tutorial2View.h
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol Tutorial2Delegate
-(void)nextTutorial2;
@end

@interface Tutorial2View : UIView{
    
    IBOutlet UIView *contentView;
    IBOutlet UIImageView *imgView;
    IBOutlet UILabel *lblText;
    IBOutlet NSLayoutConstraint *topConstraint;
    IBOutlet NSLayoutConstraint *imageWidthConstraint;
    IBOutlet NSLayoutConstraint *lblWidthConstraint;
}

@property(nonatomic, retain) UIView *view;
@property (nonatomic, weak) id<Tutorial2Delegate> delegate;

@end
