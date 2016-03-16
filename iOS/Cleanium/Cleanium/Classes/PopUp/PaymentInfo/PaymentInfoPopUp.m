//
//  PaymentInfoPopUp.m
//  Cleanium
//
//  Created by  DN-117 on 8/6/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "PaymentInfoPopUp.h"
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>
#import "MBProgressHUD.h"
#import "ActionSheetCustomPicker.h"

@implementation PaymentInfoPopUp

-(id)initWithCoder:(NSCoder *)aDecoder{
    
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setUp];
    }
    
    return self;
}

-(id)initWithFrame:(CGRect)frame{
    
    self = [super initWithFrame:frame];
    if (self) {
        [self setUp];
    }
    
    return self;
}

-(void)setUp{
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"PaymentInfoPopUp" owner:self options:nil] lastObject];
    [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.frame.size.width, self.frame.size.height)];
    [self addSubview:self.view];
    
    [self setTextFields];
    
    monthSelectedIndex = 0;
    monthArray = [[NSMutableArray alloc] initWithObjects:@"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9", @"10", @"11", @"12", nil];
    [btnMonth setTitle:[monthArray objectAtIndex:monthSelectedIndex] forState:UIControlStateNormal];
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy"];
    NSString *yearString = [formatter stringFromDate:[NSDate date]];
    int yearInt = [yearString intValue];
    int yearIntMax = yearInt + 12;
    yearArray = [[NSMutableArray alloc] init];
    for (int i = yearInt; i < yearIntMax; i++) {
        [yearArray addObject:[NSString stringWithFormat:@"%i", i]];
    }
    yearSelectedIndex = 0;
    [btnYear setTitle:[yearArray objectAtIndex:yearSelectedIndex] forState:UIControlStateNormal];
    
    isIphone4 = [UIScreen mainScreen].bounds.size.height == 480;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
}

-(void)setTextFields{
    
    txtCard.rightViewMode = UITextFieldViewModeAlways;
    UIImageView *rightImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCard.png"]];
    txtCard.rightView = rightImageView;
    [self setReturnButton:txtCard];
    
    txtCVC.rightViewMode = UITextFieldViewModeAlways;
    txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCvc.png"]];
    [self setReturnButton:txtCVC];
}

-(void)setReturnButton:(UITextField*)textField{
    
    NSString *btnTitle;
    SEL selectorAction;
    if (textField == txtCard) {
        btnTitle = @"Next";
        selectorAction = @selector(nextClicked:);
    }else{
        btnTitle = @"Done";
        selectorAction = @selector(doneClicked:);
    }
    
    UIToolbar* keyboardDoneButtonView = [[UIToolbar alloc] init];
    [keyboardDoneButtonView sizeToFit];
    UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:btnTitle
                                                                   style:UIBarButtonItemStyleBordered target:self
                                                                  action:selectorAction];
    [keyboardDoneButtonView setItems:[NSArray arrayWithObjects:flexibleSpace, doneButton, nil]];
    textField.inputAccessoryView = keyboardDoneButtonView;
}

- (void)doneClicked:(id)sender
{
    [self.view endEditing:YES];
//    [self checkFields];
}

- (void)nextClicked:(id)sender
{
    [txtCVC becomeFirstResponder];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(IBAction)okAction:(id)sender{
    
    [self endEditing:YES];
    [self checkFields];
    
}

-(IBAction)cancelAction:(id)sender{
    
    [self.superview removeFromSuperview];
    
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

-(void)checkFields{
    isValidationOK = YES;
    [self checkCard];
}

-(void)checkCard{
    if ([txtCard.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound && txtCard.text.length > 0) {
        [self checkMonthYear:YES];
    }else{
        txtCard.text = @"";
        txtCard.placeholder = @"Credit Card Number not valid";
        [txtCard becomeFirstResponder];
        [self setEmptyTextField:txtCard];
        txtCard.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCard.png"]];
        
        isValidationOK = NO;
        
        [self checkMonthYear:YES];
    }
}

-(void)checkMonthYear:(bool)notFromPicker{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy MM"];
    NSDate *selectedDate = [formatter dateFromString:[NSString stringWithFormat:@"%@ %@", btnYear.titleLabel.text, btnMonth.titleLabel.text]];
    NSDate *currentDate = [formatter dateFromString:[formatter stringFromDate:[NSDate date]]];
    
    if ([selectedDate compare:currentDate] == NSOrderedAscending) {
        imgExpiration.image = [UIImage imageNamed:@"redExpiration.png"];
        [lblExpirationDate setTextColor:[UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1]];
        
        isValidationOK = NO;
    }else{
        imgExpiration.image = [UIImage imageNamed:@"blueExpiration.png"];
        [lblExpirationDate setTextColor:[UIColor colorWithRed:199/255.0 green:199/255.0 blue:205/255.0 alpha:1]];
    }
    
    if (notFromPicker) {
        [self checkCvc];
    }
}

-(void)checkCvc{
    if ([txtCVC.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound && txtCVC.text.length > 0) {
        if (isValidationOK) {
            [self createCard];
        }
    }else{
        txtCVC.text = @"";
        txtCVC.placeholder = @"CVC Number not valid";
        if (isValidationOK) {
            [txtCVC becomeFirstResponder];
        }
        [self setEmptyTextField:txtCVC];
        txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCvc.png"]];
    }
}

-(void)createCard{
    
    STPCard *card = [[STPCard alloc] init];
    card.number = txtCard.text;
    card.expMonth = [btnMonth.titleLabel.text integerValue];
    card.expYear = [btnYear.titleLabel.text integerValue];
    card.cvc = txtCVC.text;
    
    [self getStripeToken:card];
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
                                        [self stripeClientCreated];
                                    }
                                }];
    
}

-(void)stripeClientCreated{

    [self.superview removeFromSuperview];
    [self.delegate okPaymentInfo];
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    if (textField == txtCard && [txtCVC isFirstResponder]) {
        [txtCVC resignFirstResponder];
        [txtCard becomeFirstResponder];
    }else if (textField == txtCVC && [txtCard isFirstResponder]) {
        [txtCard resignFirstResponder];
        [txtCVC becomeFirstResponder];
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == txtCard) {
        [txtCVC becomeFirstResponder];
    }else {
//        [self checkFields];
    }
    
    return YES;
}

- (BOOL) textField:(UITextField*)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString*)textEntered {
    
    if (textField.text.length == 0 && ![textEntered isEqualToString:@""]){
        if (textField == txtCard) {
            txtCard.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueCard.png"]];
        }else if (textField == txtCVC) {
            txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueCvc.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        if (textField == txtCard) {
            txtCard.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCard.png"]];
        }else if (textField == txtCVC) {
            txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCvc.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        if (textField == txtCard) {
            txtCard.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCard.png"]];
        }else if (textField == txtCVC) {
            txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCvc.png"]];
        }
    }
    return YES;
}

-(void)keyboardDidShow:(NSNotification *)notification
{
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(closeKeyboard:)];
    tapBackground.delegate = self;
    [self.superview addGestureRecognizer:tapBackground];
    
    viewY = self.frame.origin.y;
    if ([txtCard isFirstResponder]) {
        return;
    }
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
    
    CGRect newFrame = self.frame;
    newFrame.origin.y = keyboardEndFrame.origin.y - newFrame.size.height;
    self.frame = newFrame;
    
    [UIView commitAnimations];

}

-(void)moveViewUp:(float)y{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.25];
    [UIView setAnimationCurve:7];
    
    viewY = self.frame.origin.y;
    CGRect newFrame = self.frame;
    newFrame.origin.y = newFrame.origin.y - y;
    self.frame = newFrame;
    
    [UIView commitAnimations];
}

-(void)moveViewDown{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.25];
    [UIView setAnimationCurve:7];
    
    CGRect newFrame = self.frame;
    newFrame.origin.y = viewY;
    self.frame = newFrame;
    
    [UIView commitAnimations];
}

-(void)keyboardDidHide:(NSNotification *)notification
{
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
    
    CGRect newFrame = self.frame;
    newFrame.origin.y = viewY;
    self.frame = newFrame;
    
    [UIView commitAnimations];
    
    [self.superview removeGestureRecognizer:[self.superview.gestureRecognizers firstObject]];
}

-(void)closeKeyboard:(UITapGestureRecognizer*)sender{
    
    [sender.view endEditing:YES];
}

-(IBAction)chooseMonthAction:(id)sender{
    StringCustomPicker *monthCustomPicker = [[StringCustomPicker alloc] init];
    monthCustomPicker.delegate = self;
    monthCustomPicker.stringArray = monthArray;
    monthCustomPicker.selectedIndex = monthSelectedIndex;
    monthCustomPicker.selectedString = [monthArray objectAtIndex:monthSelectedIndex];
    monthCustomPicker.type = @"month";
    
    NSNumber *selIndex = [NSNumber numberWithInteger:monthSelectedIndex];
    
    NSArray *initialSelections = @[selIndex];
    
    [ActionSheetCustomPicker showPickerWithTitle:@"Select month" delegate:monthCustomPicker showCancelButton:YES origin:sender
                               initialSelections:initialSelections];
    
    if (isIphone4 && ![txtCVC  isFirstResponder]) {
        [self moveViewUp:50];
    }
}

-(void)stringPickerDone:(NSString *)selectedAddress withIndex:(NSInteger)index withType:(NSString *)type{
    if (isIphone4 && ![txtCVC  isFirstResponder]) {
        [self moveViewDown];
    }
    
    if ([type isEqualToString:@"month"]) {
        monthSelectedIndex = index;
        [btnMonth setTitle:[monthArray objectAtIndex:monthSelectedIndex] forState:UIControlStateNormal];
    }else if ([type isEqualToString:@"year"]) {
        yearSelectedIndex = index;
        [btnYear setTitle:[yearArray objectAtIndex:yearSelectedIndex] forState:UIControlStateNormal];
    }
    
    [self checkMonthYear:NO];
}

-(void)stringPickerCancel{
    if (isIphone4 && ![txtCVC  isFirstResponder]) {
        [self moveViewDown];
    }
}

-(IBAction)chooseYearAction:(id)sender{
    StringCustomPicker *yearCustomPicker = [[StringCustomPicker alloc] init];
    yearCustomPicker.delegate = self;
    yearCustomPicker.stringArray = yearArray;
    yearCustomPicker.selectedIndex = yearSelectedIndex;
    yearCustomPicker.selectedString = [yearArray objectAtIndex:yearSelectedIndex];
    yearCustomPicker.type = @"year";
    
    NSNumber *selIndex = [NSNumber numberWithInteger:yearSelectedIndex];
    
    NSArray *initialSelections = @[selIndex];
    
    [ActionSheetCustomPicker showPickerWithTitle:@"Select year" delegate:yearCustomPicker showCancelButton:YES origin:sender
                               initialSelections:initialSelections];
    
    if (isIphone4 && ![txtCVC  isFirstResponder]) {
        [self moveViewUp:50];
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
