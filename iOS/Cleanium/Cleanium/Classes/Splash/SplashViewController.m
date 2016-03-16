//
//  SplashViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SplashViewController.h"
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>
#import "WashStatusViewController.h"
#import "SlideNavigationController.h"
#import "NetworkUtility.h"
#import "LoginSignUpViewController.h"

@interface SplashViewController ()

@end

@implementation SplashViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.navigationController setNavigationBarHidden:YES];
    [self setNavigationBar];
    
    if ([UIScreen mainScreen].bounds.size.height == 667) {
        topLogoConstraint.constant = 50;
    }else if ([UIScreen mainScreen].bounds.size.height == 568) {
        topLogoConstraint.constant = 42;
    }else if ([UIScreen mainScreen].bounds.size.height == 480) {
        topLogoConstraint.constant = 32;
    }else if ([UIScreen mainScreen].bounds.size.height == 736) {
        topLogoConstraint.constant = 55;
    }else if ([UIScreen mainScreen].bounds.size.height == 1024) {
        topLogoConstraint.constant = 45;
    }
    
    self.title = @"";
    
    appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
//    [self performSelector:@selector(pushLoginSignUp) withObject:nil afterDelay:1];
    
    [self getConfig];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setNavigationBar{
    
    [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    if ([UIScreen mainScreen].bounds.size.width == 320) {
        self.navigationController.navigationBar.shadowImage = [UIImage imageNamed:@"shadow320.png"];
    }else if ([UIScreen mainScreen].bounds.size.width == 375) {
        self.navigationController.navigationBar.shadowImage = [UIImage imageNamed:@"shadow375.png"];
    }else if ([UIScreen mainScreen].bounds.size.width == 414) {
        self.navigationController.navigationBar.shadowImage = [UIImage imageNamed:@"shadow414.png"];
    }else if ([UIScreen mainScreen].bounds.size.width == 768) {
        self.navigationController.navigationBar.shadowImage = [UIImage imageNamed:@"shadow768.png"];
    }
//    self.navigationController.navigationBar.shadowImage = [UIImage imageNamed:@"shadow"];
    self.navigationController.navigationBar.translucent = YES;
    self.navigationController.view.backgroundColor = [UIColor clearColor];
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:109/255.0f green:193/255.0f blue:227/255.0f alpha:1];
    
    UIButton *btnLogo = [UIButton buttonWithType:UIButtonTypeCustom];
    btnLogo.bounds = CGRectMake( 0, 0, 34, 34);
    [btnLogo setImage:[UIImage imageNamed:@"logo.png"] forState:UIControlStateNormal];
    UIBarButtonItem *logoButton = [[UIBarButtonItem alloc] initWithCustomView:btnLogo];
    
    self.navigationItem.rightBarButtonItem = logoButton;
    
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:self.navigationItem.backBarButtonItem.style target:nil action:nil];
}

-(void)pushLoginSignUp{
    
    bool isNotFirst = [[NSUserDefaults standardUserDefaults]
                            boolForKey:@"isNotFirst"];
    if (!isNotFirst) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"isNotFirst"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        [self performSegueWithIdentifier:@"TutorialViewController" sender:nil];
    }else{
        if ([PFUser currentUser]) {
            NSNumber *isEmailVerified = [[PFUser currentUser] objectForKey:@"emailVerified"];
            if ([isEmailVerified boolValue]) {
                //        WashStatusViewController *washStatusViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"WashStatusViewController"];
                
                MenuViewController *leftMenu = (MenuViewController*)[self.storyboard
                                                                     instantiateViewControllerWithIdentifier: @"MenuViewController"];
                leftMenu.delegate = self;
                
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
            }else{
                [self performSegueWithIdentifier:@"LoginSignUpViewController" sender:nil];
            }
            
        }else{
            [self performSegueWithIdentifier:@"LoginSignUpViewController" sender:nil];
        }
    }
}

-(void)fromLogout{
    [self performSegueWithIdentifier:@"LoginSignUpViewController" sender:nil];
}

-(void)getConfig{
    
    if ([NetworkUtility checkInternetConnection]) {
        
        isGetConfigFinished = NO;
        isGetConfigOk = NO;
        isTimerFinished = NO;
        timer = [NSTimer timerWithTimeInterval:1.0f target:self selector:@selector(checkGetConfig:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
        
        [PFConfig getConfigInBackgroundWithBlock:^(PFConfig *config, NSError *error) {
            isGetConfigFinished = YES;
            if (!error) {
                NSLog(@"Yay! Config was fetched from the server.");
                isGetConfigOk = YES;
                
                if (isTimerFinished) {
                    [self performSelector:@selector(pushLoginSignUp) withObject:nil afterDelay:0];
                }
            } else {
                //            NSLog(@"Failed to fetch. Using Cached Config.");
                //            config = [PFConfig currentConfig];
                isGetConfigOk = NO;
                
                if (isTimerFinished) {
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:error.localizedDescription delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                    [alert show];
                }
            }
            
            appDelegate.stripeKey = config[@"stripeApiKey"];
            [Stripe setDefaultPublishableKey:appDelegate.stripeKey];
            NSLog(@"stripe key %@", appDelegate.stripeKey);
            
            appDelegate.postalCodes = config[@"postCodes"];
            appDelegate.orderStatuses = config[@"orderStatuses"];
            float driverAvailabilityInterval = [config[@"driverAvailabilityInterval"] floatValue];
            appDelegate.driverInterval = driverAvailabilityInterval / 60000;
            
        }];
        
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No internet connection" message:@"Please turn on Internet connection" delegate:self cancelButtonTitle:@"Try again" otherButtonTitles:nil];
        [alert show];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        [self getConfig];
    }
}

-(void)checkGetConfig:(id)sender{
    NSLog(@"Tajmer splash");
    [timer invalidate];
    timer = nil;
    isTimerFinished = YES;
    if (isGetConfigFinished) {
        if (isGetConfigOk) {
            NSLog(@"iz tajmera");
            [self performSelector:@selector(pushLoginSignUp) withObject:nil afterDelay:0];
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No internet connection" message:@"Please turn on Internet connection" delegate:self cancelButtonTitle:@"Try again" otherButtonTitles:nil];
            [alert show];
        }
    }
    
    
}


-(void)okTutorialViewController{
    [self performSegueWithIdentifier:@"LoginSignUpViewController" sender:[NSNumber numberWithBool:YES]];
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"TutorialViewController"]) {
        TutorialViewController *tutorialViewController = [segue destinationViewController];
        tutorialViewController.delegate = self;
    }else if ([[segue identifier] isEqualToString:@"LoginSignUpViewController"]) {
        LoginSignUpViewController *loginSignUpViewController = [segue destinationViewController];
        if ([sender boolValue]) {
            loginSignUpViewController.fromTutorial = YES;
        }else{
            loginSignUpViewController.fromTutorial = NO;
        }
        
    }
}


@end
