//
//  SignUpMainViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUpMainViewController.h"
#import "SignUpViewController.h"
#import "SignUp2ViewController.h"

@interface SignUpMainViewController ()

@end

@implementation SignUpMainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.navigationController setNavigationBarHidden:NO];
    
    self.title = @"SIGNUP";
    
    [self setNavigationBar];
    [self setPageViewController];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setNavigationBar{
    
    UIButton *btnLogo = [UIButton buttonWithType:UIButtonTypeCustom];
    btnLogo.bounds = CGRectMake( 0, 0, 34, 34);
    [btnLogo setImage:[UIImage imageNamed:@"logo.png"] forState:UIControlStateNormal];
    UIBarButtonItem *logoButton = [[UIBarButtonItem alloc] initWithCustomView:btnLogo];
    
    self.navigationItem.rightBarButtonItem = logoButton;

}

-(void)setPageViewController{
    
    pageViewController = [self.storyboard instantiateViewControllerWithIdentifier: @"SignUpPageViewController"];
    pageViewController.pageDelegate = self;
    
    UIView *pageView = pageViewController.view;
    [pageView setBackgroundColor:[UIColor greenColor]];
    pageView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:pageView];
    
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[pageView]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(pageView, self.view)]];
    id topGuide = self.topLayoutGuide;
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[topGuide][pageView]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(topGuide, pageView)]];
}

-(void)goToLogin{
    
    [self.navigationController popViewControllerAnimated:NO];
    [self.delegate goToLogin];
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
