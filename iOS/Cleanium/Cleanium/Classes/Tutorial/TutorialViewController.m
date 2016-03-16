//
//  TutorialViewController.m
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "TutorialViewController.h"

@interface TutorialViewController ()

@end

@implementation TutorialViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    Tutorial1View *tut1View = [[Tutorial1View alloc] init];
    tut1View.delegate = self;
    Tutorial2View *tut2View = [[Tutorial2View alloc] init];
    tut2View.delegate = self;
    Tutorial3View *tut3View = [[Tutorial3View alloc] init];
    tut3View.delegate = self;
    
    tutorialViews = [[NSMutableArray alloc] init];
    [tutorialViews addObject:tut1View];
    [tutorialViews addObject:tut2View];
    [tutorialViews addObject:tut3View];
    
    pageControl.numberOfPages = tutorialViews.count;
    
    [self setViews];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setViews{
    
    UIView *prevView;
    for (int i = 0; i < tutorialViews.count; i++) {
        UIView *tutViewFromArray = [tutorialViews objectAtIndex:i];
        [tutViewFromArray setTranslatesAutoresizingMaskIntoConstraints:NO];
        [scrollView addSubview:tutViewFromArray];
        
        if (i == 0) {
            [scrollView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[tutViewFromArray(==scrollView)]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(tutViewFromArray, scrollView)]];
            prevView = tutViewFromArray;
        }else{
            [scrollView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[prevView][tutViewFromArray(==prevView)]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(tutViewFromArray, prevView)]];
            prevView = tutViewFromArray;
        }
        [scrollView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[tutViewFromArray(==scrollView)]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(tutViewFromArray, scrollView)]];
        if (i == tutorialViews.count - 1) {
            [scrollView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[prevView]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(tutViewFromArray, prevView)]];
        }
        
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)sender {
    CGFloat pageWidth = scrollView.frame.size.width;
    NSInteger offsetLooping = 1;
    int page = (floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + offsetLooping);
    
    pageControl.currentPage = page % [tutorialViews count];
    //self.title = [[[winery wineArray] objectAtIndex:page] wineName];
    [pageControl setHidden:NO];

}

-(void)nextTutorial1{
    
    CGRect frame = scrollView.frame;
    frame.origin.x = frame.size.width * 1;
    frame.origin.y = 0;
    [scrollView scrollRectToVisible:frame animated:YES];
    
    pageControl.currentPage = 1;
}

-(void)nextTutorial2{
    
    CGRect frame = scrollView.frame;
    frame.origin.x = frame.size.width * 2;
    frame.origin.y = 0;
    [scrollView scrollRectToVisible:frame animated:YES];
    
    pageControl.currentPage = 2;
}

-(void)getStartedTutorial3{
//    [self.navigationController popViewControllerAnimated:NO];
    [self.delegate okTutorialViewController];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
