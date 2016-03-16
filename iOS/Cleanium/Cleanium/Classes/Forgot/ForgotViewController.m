//
//  ForgotViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/23/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "ForgotViewController.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"

#define SYSTEM_VERSION_LESS_THAN(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

@interface ForgotViewController ()

@end

@implementation ForgotViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.navigationController setNavigationBarHidden:NO];
    
    self.title = @"FORGOT PASSWORD";
    
    if ([UIScreen mainScreen].bounds.size.height == 667) {
        topLogoConstraint.constant = 90;
    }else if ([UIScreen mainScreen].bounds.size.height == 568) {
        topLogoConstraint.constant = 82;
    }else if ([UIScreen mainScreen].bounds.size.height == 480) {
        topLogoConstraint.constant = 40;
    }else if ([UIScreen mainScreen].bounds.size.height == 736) {
        topLogoConstraint.constant = 115;
    }else if ([UIScreen mainScreen].bounds.size.height == 1024) {
        topLogoConstraint.constant = 115;
    }
    
    if (SYSTEM_VERSION_LESS_THAN(@"8.0")) {
        [emailView removeConstraint:heightViewsConstraint];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:emailView
                                                              attribute:NSLayoutAttributeHeight
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:nil
                                                              attribute:NSLayoutAttributeNotAnAttribute
                                                             multiplier:1.0
                                                               constant:41.5]];
    }
    
    [self setNavigationBar];
    
    [self setBorder:emailView];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    UITapGestureRecognizer *tapEmailView = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapEmailView:)];
    [emailView addGestureRecognizer:tapEmailView];
    
    [btnReset setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:90/255.0f green:159/255.0f blue:187/255.0f alpha:1]] forState:UIControlStateHighlighted];
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

-(void)setBorder:(UIView*)textField{
    textField.layer.borderWidth = 0.3;
    textField.layer.borderColor = [UIColor lightGrayColor].CGColor;
}

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

- (void)tapEmailView:(id)sender {
    if (![txtEmail isFirstResponder]) {
        [txtEmail becomeFirstResponder];
    }
}

-(BOOL) validateEmail: (NSString *) candidate {
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex]; //  return 0;
    return [emailTest evaluateWithObject:candidate];
}

-(void)checkMail{
    if([self validateEmail:txtEmail.text]) {
        [txtEmail resignFirstResponder];
        [self performReset];
    }else{
        txtEmail.text = @"";
//        txtEmail.placeholder = @"E-Mail not valid";
        [txtEmail becomeFirstResponder];
        [self setEmptyTextField:txtEmail];
        imgEmail.image = [UIImage imageNamed:@"redMail.png"];
    }
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

-(void)checkFields{
    if ([txtEmail.text length] == 0) {
        [txtEmail becomeFirstResponder];
        [self setEmptyTextField:txtEmail];
        imgEmail.image = [UIImage imageNamed:@"redMail.png"];
    }else if ([txtEmail.text length] > 0){
//        [txtEmail resignFirstResponder];
        [self checkMail];
    }
}

- (BOOL) textField:(UITextField*)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString*)textEntered {
    
    if (textField.text.length == 0 && ![textEntered isEqualToString:@""]){
        
        imgEmail.image = [UIImage imageNamed:@"blueMail.png"];
        
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        
        imgEmail.image = [UIImage imageNamed:@"grayMail.png"];
        
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        
        imgEmail.image = [UIImage imageNamed:@"redMail.png"];
        
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    
    [self checkFields];
    
    return YES;
}

-(IBAction)resetAction:(id)sender{
    
    [self checkFields];
}

-(void)performReset{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Reset password";
    
    [PFUser requestPasswordResetForEmailInBackground:txtEmail.text block:^(BOOL result, NSError *error)
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
             if (result) {
                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"Confirmation link for changing your password has been sent to your email account" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                 [alert show];
                 [self.navigationController popViewControllerAnimated:YES];
             }else{
                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Something went wrong" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
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

-(void)textFieldDidBeginEditing:(UITextField *)sender
{
    if ([UIScreen mainScreen].bounds.size.height <= 568) {
        [self setViewMovedUp:YES withTextField:sender];
    }
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    
    if ([UIScreen mainScreen].bounds.size.height <= 568) {
        [self setViewMovedUp:NO withTextField:textField];
    }
    
}

-(void)setViewMovedUp:(BOOL)movedUp withTextField:(UITextField*)txtField
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2]; // if you want to slide up the view
    
    if (movedUp)
    {
//        [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y - kOFFSET_FOR_KEYBOARD, self.view.frame.size.width, self.view.frame.size.height)];
        topLogoConstraint.constant = topLogoConstraint.constant - kOFFSET_FOR_KEYBOARD;
    }
    else
    {
//        [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y + kOFFSET_FOR_KEYBOARD, self.view.frame.size.width, self.view.frame.size.height)];
        topLogoConstraint.constant = topLogoConstraint.constant + kOFFSET_FOR_KEYBOARD;
    }
    [self.view layoutIfNeeded];
    
    
    [UIView commitAnimations];
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
