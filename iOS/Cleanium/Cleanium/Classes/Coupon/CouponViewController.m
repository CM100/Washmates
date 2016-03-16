//
//  CouponViewController.m
//  Cleanium
//
//  Created by marko on 9/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "CouponViewController.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"
#import "DiscountCode.h"

#define SYSTEM_VERSION_LESS_THAN(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

@interface CouponViewController ()

@end

@implementation CouponViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"COUPONS";
    
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
        [codeView removeConstraint:heightViewsConstraint];
        [self.view addConstraint:[NSLayoutConstraint constraintWithItem:codeView
                                                              attribute:NSLayoutAttributeHeight
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:nil
                                                              attribute:NSLayoutAttributeNotAnAttribute
                                                             multiplier:1.0
                                                               constant:41.5]];
    }
    
    [self setNavigationBar];
    
    [self setBorder:codeView];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    UITapGestureRecognizer *tapCodeView = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapCodeView:)];
    [codeView addGestureRecognizer:tapCodeView];
    
    [btnSubmit setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:90/255.0f green:159/255.0f blue:187/255.0f alpha:1]] forState:UIControlStateHighlighted];
    
    NSDictionary *discountCodeDic = [[NSUserDefaults standardUserDefaults] objectForKey:@"discountCode"];
    if (discountCodeDic) {
        txtCode.text = [discountCodeDic objectForKey:@"code"];
        NSString *characterPercentage;
        if ([[discountCodeDic objectForKey:@"isPercentage"] boolValue]) {
            characterPercentage = @"%";
        }else{
            characterPercentage = @"$";
        }
        lblDiscountCode.text = [NSString stringWithFormat:@"%@%@ will be subtracted from your next order", [discountCodeDic objectForKey:@"amount"], characterPercentage];
    }else{
        lblDiscountCode.text = @"";
    }
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - SlideNavigationController Methods -

- (BOOL)slideNavigationControllerShouldDisplayLeftMenu
{
    return YES;
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

- (void)tapCodeView:(id)sender {
    if (![txtCode isFirstResponder]) {
        [txtCode becomeFirstResponder];
    }
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

-(void)checkFields{
    if ([txtCode.text length] == 0) {
        [txtCode becomeFirstResponder];
        [self setEmptyTextField:txtCode];
        imgEmail.image = [UIImage imageNamed:@"redUser.png"];
    }else if ([txtCode.text length] > 0){
        //        [txtEmail resignFirstResponder];
        [self checkCode];
    }
}

-(void)checkCode{
    if(txtCode.text.length > 0) {
        [txtCode resignFirstResponder];
        [self performCodeSubmit];
    }else{
        txtCode.text = @"";
        [txtCode becomeFirstResponder];
        [self setEmptyTextField:txtCode];
        imgEmail.image = [UIImage imageNamed:@"redUser.png"];
    }
}

- (BOOL) textField:(UITextField*)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString*)textEntered {
    
    if (textField.text.length == 0 && ![textEntered isEqualToString:@""]){
        
        imgEmail.image = [UIImage imageNamed:@"blueUser.png"];
        
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        
        imgEmail.image = [UIImage imageNamed:@"grayUser.png"];
        
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        
        imgEmail.image = [UIImage imageNamed:@"redUser.png"];
        
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    
    [self checkFields];
    
    return YES;
}

-(IBAction)submitAction:(id)sender{
    
    [self checkFields];
}

-(void)performCodeSubmit{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Veryfication of discount code";
    
    [PFCloud callFunctionInBackground:@"verifyDiscountCode"
                       withParameters:@{@"discountCode": txtCode.text}
                                block:^(PFObject *response, NSError *error) {
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
                                        NSMutableDictionary *discountCodeDic = [DiscountCode toDictionaryFromResponse:response];
                                        
                                        [[NSUserDefaults standardUserDefaults] setObject:discountCodeDic forKey:@"discountCode"];
                                        [[NSUserDefaults standardUserDefaults] synchronize];
                                        
                                        NSString *characterPercentage;
                                        if ([[discountCodeDic objectForKey:@"isPercentage"] boolValue]) {
                                            characterPercentage = @"%";
                                        }else{
                                            characterPercentage = @"$";
                                        }
                                        lblDiscountCode.text = [NSString stringWithFormat:@"%@%@ will be subtracted from your next order", [discountCodeDic objectForKey:@"amount"], characterPercentage];
                                        
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
