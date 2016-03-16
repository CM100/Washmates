//
//  SignUpPageViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUpPageViewController.h"
#import "MBProgressHUD.h"

@interface SignUpPageViewController ()

@end

@implementation SignUpPageViewController

@synthesize signUpViewControllers;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    SignUpViewController *s1 = [self.storyboard
                               instantiateViewControllerWithIdentifier:@"SignUpViewController"];
    s1.delegate = self;

    
    SignUp2ViewController *s2 = [self.storyboard
                             instantiateViewControllerWithIdentifier:@"SignUp2ViewController"];
    s2.delegate = self;
    
    SignUp3ViewController *s3 = [self.storyboard
                                 instantiateViewControllerWithIdentifier:@"SignUp3ViewController"];
    s3.delegate = self;
    
    signUpViewControllers = @[s1,s2, s3];
    
    [self setViewControllers:@[s1]
                   direction:UIPageViewControllerNavigationDirectionForward
                    animated:NO completion:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(UIViewController *)viewControllerAtIndex:(NSUInteger)index
{
    return signUpViewControllers[index];
}

-(UIViewController *)pageViewController:(UIPageViewController *)pageViewController
     viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSUInteger currentIndex = [signUpViewControllers indexOfObject:viewController];
    
    --currentIndex;
    currentIndex = currentIndex % (signUpViewControllers.count);
    return [signUpViewControllers objectAtIndex:currentIndex];
}

-(UIViewController *)pageViewController:(UIPageViewController *)pageViewController
      viewControllerAfterViewController:(UIViewController *)viewController
{
    NSUInteger currentIndex = [signUpViewControllers indexOfObject:viewController];
    
    ++currentIndex;
    currentIndex = currentIndex % (signUpViewControllers.count);
    return [signUpViewControllers objectAtIndex:currentIndex];
}

-(void)performNextFromSignUp1:(PFUser *)user{
    [self performSignUp:user];
}

-(void)performSignUp:(PFUser*)user{
    signUpUser = user;
    
    SignUp2ViewController *signUp2ViewController = [self.signUpViewControllers objectAtIndex:1];
    [self setViewControllers:@[signUp2ViewController]
                   direction:UIPageViewControllerNavigationDirectionForward
                    animated:YES
                  completion:nil];
    
}

-(void)performBackFromSignUp2{
    SignUpViewController *signUp1ViewController = [self.signUpViewControllers firstObject];
    [self setViewControllers:@[signUp1ViewController]
                   direction:UIPageViewControllerNavigationDirectionReverse
                    animated:YES
                  completion:nil];
}

-(void)performNextFromSignUp2:(PFObject *)objectAddress{
    addressObject = objectAddress;
    
    SignUp3ViewController *signUp3ViewController = [self.signUpViewControllers lastObject];
    [self setViewControllers:@[signUp3ViewController]
                   direction:UIPageViewControllerNavigationDirectionForward
                    animated:YES
                  completion:nil];
}

-(void)performBackFromSignUp3{
    SignUp2ViewController *signUp2ViewController = [self.signUpViewControllers objectAtIndex:1];
    [self setViewControllers:@[signUp2ViewController]
                   direction:UIPageViewControllerNavigationDirectionReverse
                    animated:YES
                  completion:nil];
}

-(void)performFinishFromSignUp3:(STPCard *)card{
    cardObject = card;
    
    [self performSignUp];
}

-(void)performSignUp{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Sign Up";
    [signUpUser signUpInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
     {
         [hud hide:YES];
         if (error)
         {
             UIAlertView *alertView =
             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                        message:error.localizedDescription
                                       delegate:self
                              cancelButtonTitle:nil
                              otherButtonTitles:@"Ok", nil];
             [alertView show];
         } else {
             NSLog(@"success");
             [self performInsertAddress];
             
         }
     }];

}

-(void)performInsertAddress{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Saving address";
    if (signUpUser) {
        [addressObject setObject:signUpUser forKey:@"user"];
    }
    
    [addressObject saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
     {
         [hud hide:YES];
         if (error)
         {
             UIAlertView *alertView =
             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                        message:error.localizedDescription
                                       delegate:self
                              cancelButtonTitle:nil
                              otherButtonTitles:@"Ok", nil];
             [alertView show];
         } else {
             NSLog(@"success");
             
             if (cardObject) {
                 [self getStripeToken:cardObject];
             }else{
                 [self signUpFinished];
             }
             
         }
     }];
}

-(void)getStripeToken:(STPCard *)card{

    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Saving credit card";
    
    [[STPAPIClient sharedClient] createTokenWithCard:card
                                          completion:^(STPToken *token, NSError *error) {
                                              [hud hide:YES];
                                              if (error) {
                                                  UIAlertView *alertView =
                                                  [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                             message:error.localizedDescription
                                                                            delegate:self
                                                                   cancelButtonTitle:nil
                                                                   otherButtonTitles:@"Ok", nil];
                                                  [alertView show];
                                              }else {
                                                  [self createStripeCustomerObject:token];
                                              }
                                          }];
}

-(void)createStripeCustomerObject:(STPToken*)token{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Saving credit card";
    
    [PFCloud callFunctionInBackground:@"createStripeCustomer"
                       withParameters:@{@"creditCardToken": token.tokenId}
                                block:^(NSString *response, NSError *error) {
                                    [hud hide:YES];
                                    if (error) {
                                        UIAlertView *alertView =
                                        [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                   message:error.localizedDescription
                                                                  delegate:self
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"Ok", nil];
                                        [alertView show];
                                    }else {
                                        [self signUpFinished];
                                    }
                                }];
    
}

-(void)signUpFinished{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Success" message:@"Thank you for completing the sign up process. Please confirm your email and start using the app" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    if (buttonIndex == 0) {
        [self.pageDelegate goToLogin];
    }
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
