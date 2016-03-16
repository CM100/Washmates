//
//  SignUp3ViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/17/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUp3ViewController.h"
#import "SignUpHelper.h"
#import "LoginSignUpViewController.h"
#import "ActionSheetCustomPicker.h"

@interface SignUp3ViewController ()

@end

@implementation SignUp3ViewController

@synthesize signUpUser, addressObject, fromSettings;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    if (fromSettings) {
        self.title = @"PAYMENT INFORMATION";
        [self setFromSettingsFields];
    }else{
        self.title = @"SIGN UP";
    }
    [self setNavigationBar];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    
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
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardDidHideNotification object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillDisappear:(BOOL)animated{
    
    [super viewWillDisappear:animated];
    
    [txtCVC resignFirstResponder];
}

-(void)setNavigationBar{
    
    UIButton *btnLogo = [UIButton buttonWithType:UIButtonTypeCustom];
    btnLogo.bounds = CGRectMake( 0, 0, 34, 34);
    [btnLogo setImage:[UIImage imageNamed:@"logo.png"] forState:UIControlStateNormal];
    UIBarButtonItem *logoButton = [[UIBarButtonItem alloc] initWithCustomView:btnLogo];
    
    self.navigationItem.rightBarButtonItem = logoButton;
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

-(IBAction)backAction:(id)sender{
    
    if(fromSettings){
        [self checkFields];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
    
//    [self.delegate performBackFromSignUp3];
}

-(IBAction)nextAction:(id)sender{
    
    if (txtCard.text.length > 0 && txtCVC.text.length > 0) {
        [self checkFields];
    }else{
        [self clearFields];
        [SignUpHelper performSignUp:self withUser:signUpUser withAddress:addressObject withCard:nil];
        
//        [self.delegate performFinishFromSignUp3:nil];
    }
    
    
}

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

-(void)clearFields{
    [self clearTextField:txtCard];
    [self clearTextField:txtCVC];
    [lblExpirationDate setTextColor:[UIColor colorWithRed:199/255.0 green:199/255.0 blue:205/255.0 alpha:1]];
    imgExpiration.image = [UIImage imageNamed:@"grayExpiration.png"];
    monthSelectedIndex = 0;
    [btnMonth setTitle:[monthArray objectAtIndex:monthSelectedIndex] forState:UIControlStateNormal];
    yearSelectedIndex = 0;
    [btnYear setTitle:[yearArray objectAtIndex:yearSelectedIndex] forState:UIControlStateNormal];
}

-(void)clearTextField:(UITextField*)textField{
    textField.text = @"";
    if (textField == txtCard) {
        textField.placeholder = @"Credit Card Number";
        txtCard.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCard.png"]];
    }else if (textField == txtCVC) {
        textField.placeholder = @"CVC Number";
        txtCVC.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCvc.png"]];
    }
    UIColor *color = [UIColor colorWithRed:199/255.0 green:199/255.0 blue:205/255.0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 0;
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

-(void)checkMonth{
    if ([btnMonth.titleLabel.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound && [btnMonth.titleLabel.text intValue] > 0  && [btnMonth.titleLabel.text intValue] < 13) {
        [self checkYear];
    }else{
        imgExpiration.image = [UIImage imageNamed:@"redExpiration.png"];
    }
}

-(void)checkYear{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy"];
    NSString *yearString = [formatter stringFromDate:[NSDate date]];
    if ([btnYear.titleLabel.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound && btnYear.titleLabel.text.length == 4 && [btnYear.titleLabel.text intValue] >= [yearString intValue]) {
        [self checkCvc];
    }else{
        imgExpiration.image = [UIImage imageNamed:@"redExpiration.png"];
    }
}

-(void)checkCvc{
    if ([txtCVC.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound && txtCVC.text.length > 0) {
        if (isValidationOK) {
            if(fromSettings){
                [self createCardFromSettings];
            }else{
                [self createCard];
            }
        }
    }else{
        txtCVC.text = @"";
        txtCVC.placeholder = @"CVC Number not valid";
        [txtCVC becomeFirstResponder];
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
    
    [SignUpHelper performSignUp:self withUser:signUpUser withAddress:addressObject withCard:card];
//    [self.delegate performFinishFromSignUp3:card];
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

-(void)textFieldDidBeginEditing:(UITextField *)sender
{
    activeView = sender;
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    activeView = nil;
}

-(void)keyboardWasShown:(NSNotification*)notification
{
    // keyboard frame is in window coordinates
    NSDictionary *userInfo = [notification userInfo];
    CGRect keyboardInfoFrame = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    // get the height of the keyboard by taking into account the orientation of the device too
    CGRect windowFrame = [self.view.window convertRect:self.view.frame fromView:self.view];
    CGRect keyboardFrame = CGRectIntersection (windowFrame, keyboardInfoFrame);
    CGRect coveredFrame = [self.view.window convertRect:keyboardFrame toView:self.view];
    
    // add the keyboard height to the content insets so that the scrollview can be scrolled
    UIEdgeInsets contentInsets = UIEdgeInsetsMake (0.0, 0.0, coveredFrame.size.height - bottomView.frame.size.height, 0.0);
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
    
    // make sure the scrollview content size width and height are greater than 0
    [scrollView setContentSize:CGSizeMake (scrollView.contentSize.width, scrollView.contentSize.height)];
    
    // scroll to the text view
    [scrollView scrollRectToVisible:activeView.frame animated:YES];
}

-(void)keyboardWillBeHidden:(NSNotification *)notification
{
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
}

-(void)signUpFinished{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Success" message:@"Thank you for completing the sign up process. Please confirm your email and start using the app" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    if (buttonIndex == 0) {
//        [self.pageDelegate goToLogin];
        for (UIViewController *viewController in self.navigationController.viewControllers) {
            if ([viewController isKindOfClass:[LoginSignUpViewController class]]) {
                LoginSignUpViewController *loginSignUpViewController = (LoginSignUpViewController*)viewController;
                [self.navigationController popToViewController:loginSignUpViewController animated:NO];
                [loginSignUpViewController goToLoginFromSignUp];
            }
        }
    }
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
}

-(void)stringPickerDone:(NSString *)selectedAddress withIndex:(NSInteger)index withType:(NSString *)type{
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
}

-(void)setFromSettingsFields{
    
    lblDescription.text = @"";
    [btnPrevious setBackgroundImage:[UIImage imageNamed:@"place-order-icon.png"] forState:UIControlStateNormal];
    lblBtnPreviousTitle.text = @"Save";
    
    [btnNext removeFromSuperview];
    [lblBtnTitle removeFromSuperview];
    [bottomView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[btnPrevious]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(btnPrevious, bottomView)]];
}

-(void)createCardFromSettings{
    
    STPCard *card = [[STPCard alloc] init];
    card.number = txtCard.text;
    card.expMonth = [btnMonth.titleLabel.text integerValue];
    card.expYear = [btnYear.titleLabel.text integerValue];
    card.cvc = txtCVC.text;
    
    [SignUpHelper getStripeToken:self withCard:card fromSettings:YES];
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
