//
//  LoginViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "LoginViewController.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"

#define SYSTEM_VERSION_LESS_THAN(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

@interface LoginViewController ()

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
//    [self.navigationController setNavigationBarHidden:NO];
    
    self.title = @"";
    
    if ([UIScreen mainScreen].bounds.size.height == 667) {
        topLogoConstraint.constant = 80;
    }else if ([UIScreen mainScreen].bounds.size.height == 568) {
        topLogoConstraint.constant = 72;
    }else if ([UIScreen mainScreen].bounds.size.height == 480) {
        topLogoConstraint.constant = 50;
    }else if ([UIScreen mainScreen].bounds.size.height == 736) {
        topLogoConstraint.constant = 95;
    }else if ([UIScreen mainScreen].bounds.size.height == 1024) {
        topLogoConstraint.constant = 95;
    }
    
    if (SYSTEM_VERSION_LESS_THAN(@"8.0")) {
        [usernameView removeConstraint:heightViewsConstraint];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:usernameView
                                                              attribute:NSLayoutAttributeHeight
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:nil
                                                              attribute:NSLayoutAttributeNotAnAttribute
                                                             multiplier:1.0
                                                               constant:41.5]];
    }
    
    topConstantValue = topLogoConstraint.constant;
    
    [self setBorder:usernameView];
    [self setBorder:passwordView];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    UITapGestureRecognizer *tapUserView = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapUserView:)];
    [usernameView addGestureRecognizer:tapUserView];
    UITapGestureRecognizer *tapPasswordView = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapPasswordView:)];
    [passwordView addGestureRecognizer:tapPasswordView];
    
    [btnLogin setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:90/255.0f green:159/255.0f blue:187/255.0f alpha:1]] forState:UIControlStateHighlighted];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    if (![self.navigationController isNavigationBarHidden]) {
        [self.navigationController setNavigationBarHidden:YES animated:YES];
    }
}

-(IBAction)backAction:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}
-(void)setBorder:(UIView*)textField{
    textField.layer.borderWidth = 0.3;
    textField.layer.borderColor = [UIColor lightGrayColor].CGColor;
}

-(void)checkFields{
    isFirstResponder = NO;
    isValidationOK = YES;
    [self checkMail];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == txtUsername) {
        [txtPassword becomeFirstResponder];
    } else {
        [txtPassword resignFirstResponder];
//        [self checkFields];
    }
    return YES;
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

- (BOOL) textField:(UITextField*)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString*)textEntered {
    
    if (textField.text.length == 0 && ![textEntered isEqualToString:@""]){
        if (textField == txtUsername) {
            imgUser.image = [UIImage imageNamed:@"blueUser.png"];
        }else if (textField == txtPassword) {
            imgPassword.image = [UIImage imageNamed:@"bluePassword.png"];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        if (textField == txtUsername) {
            imgUser.image = [UIImage imageNamed:@"grayUser.png"];
        }else if (textField == txtPassword) {
            imgPassword.image = [UIImage imageNamed:@"grayPassword.png"];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        if (textField == txtUsername) {
            imgUser.image = [UIImage imageNamed:@"redUser.png"];
        }else if (textField == txtPassword) {
            imgPassword.image = [UIImage imageNamed:@"redPassword.png"];
        }
    }else if (textField.text.length > 1 && [textEntered isEqualToString:@""] && [textField isSecureTextEntry]){
        if (textField == txtPassword) {
            [self clearSecureTextField:textField withRange:range withText:textEntered];
        }
        return NO;
    }
//    else if (textField.isSecureTextEntry && range.location > 0 && range.length == 1 && textEntered.length == 0)
//    {
//        if (textField.tag == 5) {
//            imgPassword.image = [UIImage imageNamed:@"redPassword.png"];
//        }else{
//            imgPassword.image = [UIImage imageNamed:@"grayPassword.png"];
//        }
//        
//    }
    return YES;
}

-(void)clearSecureTextField:(UITextField*)textField withRange:(NSRange)range withText:(NSString*)textEntered{
    
    // Save the current text, in case iOS deletes the whole text
    NSString *text = textField.text;
    
    // Trigger deletion
    [textField deleteBackward];
    
    // iOS deleted the entire string
    if (textField.text.length != text.length - 1)
    {
        if (textField.text.length == 0) {
            if (textField.tag == 5) {
                imgPassword.image = [UIImage imageNamed:@"redPassword.png"];
            }else{
                imgPassword.image = [UIImage imageNamed:@"grayPassword.png"];
            }
        }
    }
}

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

- (void)tapUserView:(id)sender {
    if (![txtUsername isFirstResponder]) {
        [txtUsername becomeFirstResponder];
    }
}

- (void)tapPasswordView:(id)sender {
    if (![txtPassword isFirstResponder]) {
        [txtPassword becomeFirstResponder];
    }
}

-(IBAction)loginAction:(id)sender{

    [self checkFields];
}

-(BOOL) validateEmail: (NSString *) candidate {
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex]; //  return 0;
    return [emailTest evaluateWithObject:candidate];
}

-(void)checkMail{
    if([self validateEmail:txtUsername.text]) {
        [self checkPassword];
    }else{
        txtUsername.text = @"";
//        txtUsername.placeholder = @"Username not valid";
        [txtUsername becomeFirstResponder];
        isFirstResponder = YES;
        [self setEmptyTextField:txtUsername];
        imgUser.image = [UIImage imageNamed:@"redUser.png"];
        
        isValidationOK = NO;
        
        [self checkPassword];
    }
}

-(void)checkPassword{
    if (txtPassword.text.length < 6) {
        txtPassword.text = @"";
//        txtPassword.placeholder = @"Password is too short";
        if (!isFirstResponder) {
            [txtPassword becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtPassword];
        imgPassword.image = [UIImage imageNamed:@"redPassword.png"];
    }else{
        if (isValidationOK) {
            [txtPassword resignFirstResponder];
            [self performLogin];
        }
    }
}

-(void)performLogin{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Login";
    
    [PFUser logInWithUsernameInBackground:txtUsername.text
                                 password:txtPassword.text block:^(PFUser *user, NSError *error)
     {
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
             NSLog(@"success");
             NSNumber *isEmailVerified = [user objectForKey:@"emailVerified"];
             if ([isEmailVerified boolValue]) {
                 txtUsername.text = @"";
                 txtPassword.text = @"";
                 [self.navigationController popViewControllerAnimated:NO];
                 [self.delegate performLogin];
             }else{
                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Please confirm your email and start using the app" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                 [alert show];
             }
         }
     }];
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

#define kOFFSET_FOR_KEYBOARD 100.0

-(void)keyboardDidShow:(NSNotification *)notification
{
    if ([UIScreen mainScreen].bounds.size.height > 568) {
        return;
    }
    
    if (topLogoConstraint.constant == topConstantValue) {
        NSDictionary *userInfo = [notification userInfo];
        NSTimeInterval animationDuration;
        UIViewAnimationCurve animationCurve;
        CGRect keyboardEndFrame;
        
        [[userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
        [[userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
        [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] getValue:&keyboardEndFrame];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:animationCurve];
        
        topLogoConstraint.constant = topLogoConstraint.constant - kOFFSET_FOR_KEYBOARD;
        [self.view layoutIfNeeded];
        
        [UIView commitAnimations];
    }
}

-(void)keyboardDidHide:(NSNotification *)notification
{
    if ([UIScreen mainScreen].bounds.size.height > 568) {
        return;
    }
    
    if (topLogoConstraint.constant == topConstantValue - kOFFSET_FOR_KEYBOARD) {
        NSDictionary *userInfo = [notification userInfo];
        NSTimeInterval animationDuration;
        UIViewAnimationCurve animationCurve;
        CGRect keyboardEndFrame;
        
        [[userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
        [[userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
        [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] getValue:&keyboardEndFrame];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:animationCurve];
        
        topLogoConstraint.constant = topLogoConstraint.constant + kOFFSET_FOR_KEYBOARD;
        [self.view layoutIfNeeded];
        
        [UIView commitAnimations];
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
