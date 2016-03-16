//
//  LoginSignUpViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "LoginSignUpViewController.h"
#import "MenuViewController.h"
#import "SlideNavigationController.h"
#import "TutorialViewController.h"

#define SYSTEM_VERSION_LESS_THAN(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

@interface LoginSignUpViewController ()

@end

@implementation LoginSignUpViewController

@synthesize fromTutorial;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
//    [self.navigationController setNavigationBarHidden:YES];
    [self.navigationController.interactivePopGestureRecognizer setDelegate:self];
    
    if ([UIScreen mainScreen].bounds.size.height == 667) {
        topLogoConstraint.constant = 100;
    }else if ([UIScreen mainScreen].bounds.size.height == 568) {
        topLogoConstraint.constant = 92;
    }else if ([UIScreen mainScreen].bounds.size.height == 480) {
        topLogoConstraint.constant = 72;
    }else if ([UIScreen mainScreen].bounds.size.height == 736) {
        topLogoConstraint.constant = 115;
    }else if ([UIScreen mainScreen].bounds.size.height == 1024) {
        topLogoConstraint.constant = 105;
    }
    
    if (SYSTEM_VERSION_LESS_THAN(@"8.0")) {
        [btnLogin removeConstraint:heightViewsConstraint];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:btnLogin
                                                              attribute:NSLayoutAttributeHeight
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:nil
                                                              attribute:NSLayoutAttributeNotAnAttribute
                                                             multiplier:1.0
                                                               constant:35]];
    }
    
    self.title = @"";
    
    [btnLogin setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:90/255.0f green:159/255.0f blue:187/255.0f alpha:1]] forState:UIControlStateHighlighted];
    [btnSignUp setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:34/255.0f green:59/255.0f blue:75/255.0f alpha:1]] forState:UIControlStateHighlighted];
    
    if (fromTutorial) {
        fromTutorial = NO;
        NSMutableArray *navigationArray = [[NSMutableArray alloc] initWithArray: self.navigationController.viewControllers];
        for (UIViewController *viewController in navigationArray) {
            if ([viewController isKindOfClass:[TutorialViewController class]]) {
                [navigationArray removeObject:viewController];
                self.navigationController.viewControllers = navigationArray;
                break;
            }
        }
    }
}

-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    if (![self.navigationController isNavigationBarHidden]) {
        [self.navigationController setNavigationBarHidden:YES animated:YES];
        
    }
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer {

    if (self.navigationController.viewControllers.count > 2) {
        return YES;
    }
    return NO;
}

-(IBAction)loginAction:(id)sender{
    
    [self performSegueWithIdentifier:@"LoginViewController" sender:nil];
}

-(IBAction)signUpAction:(id)sender{
    
    SignUpViewController *signUpViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUpViewController"];
    signUpViewController.fromSettings = NO;
    [self.navigationController pushViewController:signUpViewController animated:YES];
    
//    [self performSegueWithIdentifier:@"SignUpMainViewController" sender:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UIImage *)imageWithColor:(UIColor *)color {
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}

-(void)goToLogin{
    
    [self performSegueWithIdentifier:@"LoginViewController" sender:nil];
}

-(void)goToLoginFromSignUp{
    
    [self performSegueWithIdentifier:@"LoginViewController" sender:nil];
}

-(void)performLogin{
    MenuViewController *leftMenu = (MenuViewController*)[self.storyboard
                                                         instantiateViewControllerWithIdentifier: @"MenuViewController"];
    
    SlideNavigationController *slideMenuViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SlideNavigationController"];
    slideMenuViewController.leftMenu = leftMenu;
    slideMenuViewController.menuRevealAnimationDuration = .18;
    
    
    CATransition *animation=[CATransition animation];
    animation.delegate=self;
    animation.duration=0.3;
    animation.type=kCATransitionMoveIn;
    animation.subtype=kCATransitionFromRight;
    [self.view.window.layer addAnimation:animation forKey:kCATransition];
    [self presentViewController:slideMenuViewController animated:NO completion:nil];
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ([[segue identifier] isEqualToString:@"LoginViewController"]) {
        LoginViewController *loginViewController = [segue destinationViewController];
        loginViewController.delegate = self;
    }else if ([[segue identifier] isEqualToString:@"SignUpMainViewController"]) {
        SignUpMainViewController *signUpMainViewController = [segue destinationViewController];
        signUpMainViewController.delegate = self;
    }
}


@end
