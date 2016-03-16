//
//  RatingViewController.m
//  Cleanium
//
//  Created by  DN-117 on 9/1/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "RatingViewController.h"
#import "ServiceClient.h"
#import "AppDelegate.h"
#import "SlideNavigationController.h"

@interface RatingViewController ()

@end

@implementation RatingViewController

@synthesize order;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"RATE OUR SERVICE";
    [self.navigationItem setHidesBackButton:YES];
    
    [self setRatingView:ratingViewService];
    [self setRatingView:ratingViewPrice];
    [self setRatingView:ratingViewTiming];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setRatingView:(TPFloatRatingView*)ratingView{
    ratingView.delegate = self;
    ratingView.emptySelectedImage = [UIImage imageNamed:@"rate-gray-star.png"];
    ratingView.fullSelectedImage = [UIImage imageNamed:@"rate-blue-star.png"];
    ratingView.contentMode = UIViewContentModeScaleAspectFill;
    ratingView.maxRating = 5;
    ratingView.minRating = 0;
    ratingView.rating = 0;
    ratingView.editable = YES;
    ratingView.halfRatings = NO;
    ratingView.floatRatings = NO;
}

-(IBAction)submitAction:(id)sender{
      
    PFObject *rating = [PFObject objectWithClassName:@"Rating"];
    [rating setObject:[NSNumber numberWithFloat:ratingViewPrice.rating/5] forKey:@"price"];
    [rating setObject:[NSNumber numberWithFloat:ratingViewService.rating/5] forKey:@"service"];
    [rating setObject:[NSNumber numberWithFloat:ratingViewTiming.rating/5] forKey:@"timing"];
    if (order) {
        [rating setObject:order forKey:@"order"];
    }
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchSetRatingWithCompletionBlock:^(id objects) {
        
        [self.delegate fromRating];
        [[SlideNavigationController sharedInstance] popViewControllerAnimated:YES];
    } failureBlock:^(NSError *error) {
        
    } withRating:rating withView:self.view];
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
