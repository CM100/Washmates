//
//  SignUp2ViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUp2ViewController.h"
#import "AppDelegate.h"
#import "AddressHelper.h"
#import "SlideNavigationController.h"

@interface SignUp2ViewController ()

@end

@implementation SignUp2ViewController

@synthesize signUpUser, fromSettings, addressFromSettings;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self setNavigationBar];
    
    [self setTextFields];
    
    if (fromSettings) {
        self.title = @"ADDRESS INFORMATION";
        if (addressFromSettings) {
            [self setAddressFromSettingFields];
        }else{
            [self setFromSettingsFields];
        }
        
    }else{
        self.title = @"SIGN UP";
    }
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardDidHideNotification object:nil];
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
    
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:self.navigationItem.backBarButtonItem.style target:nil action:nil];
}

-(void)setTextFields{
    
    txtAddressLine1.rightViewMode = UITextFieldViewModeAlways;
    UIImageView *rightImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayAddress1.png"]];
    txtAddressLine1.rightView = rightImageView;
    
    txtAddressLine2.rightViewMode = UITextFieldViewModeAlways;
    txtAddressLine2.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueAddress2.png"]];
    
    txtCity.rightViewMode = UITextFieldViewModeAlways;
    txtCity.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCity.png"]];
    
    txtState.rightViewMode = UITextFieldViewModeAlways;
    txtState.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayState.png"]];
    
    txtPostCode.rightViewMode = UITextFieldViewModeAlways;
    txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayMail.png"]];
    
    txtLocation.rightViewMode = UITextFieldViewModeAlways;
    txtLocation.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayLocation.png"]];
    
    [btnHome setSelected:YES];
    selectedLocation = @"Home";
    
    imgNotes.image = [UIImage imageNamed:@"blueNotes.png"];
    
    UIToolbar* keyboardDoneButtonView = [[UIToolbar alloc] init];
    [keyboardDoneButtonView sizeToFit];
    UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done"
                                                                   style:UIBarButtonItemStyleBordered target:self
                                                                  action:@selector(pressedBackground:)];
    [keyboardDoneButtonView setItems:[NSArray arrayWithObjects:flexibleSpace, doneButton, nil]];
    txtPostCode.inputAccessoryView = keyboardDoneButtonView;
}

-(void)setEmptyTextField:(UITextField*) textField{
    UIColor *color = [UIColor colorWithRed:214/255.0 green:0 blue:0 alpha:1];
    textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:textField.placeholder attributes:@{NSForegroundColorAttributeName: color}];
    textField.tag = 5;
}

-(void)checkFields{
    isFirstResponder = NO;
    isValidationOK = YES;
    [self checkAddressLine1];
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == txtAddressLine1) {
        [txtAddressLine2 becomeFirstResponder];
    }else if (textField == txtAddressLine2) {
        [txtCity becomeFirstResponder];
    }else if (textField == txtCity) {
        [txtState becomeFirstResponder];
    }else if (textField == txtState) {
        [txtPostCode becomeFirstResponder];
    }else if (textField == txtPostCode) {
        [txtPostCode resignFirstResponder];
    }else {
        [self checkFields];
    }
    
    return YES;
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    
    if (textField == txtLocation) {
        return NO;
    }
    
    return YES;
}

- (void)textViewDidBeginEditing:(UITextView *)textView
{
    if ([textView.text isEqualToString:@"Access Notes"]) {
        textView.text = @"";
        textView.textColor = [UIColor blackColor]; //optional
    }
    [textView becomeFirstResponder];
    
    activeView = textView;
}

-(void)textFieldDidBeginEditing:(UITextField *)sender
{
     activeView = sender;
}

- (void)textViewDidEndEditing:(UITextView *)textView
{
    if ([textView.text isEqualToString:@""]) {
        textView.text = @"Access Notes";
        textView.textColor = [UIColor colorWithRed:199/255.0f green:199/255.0f blue:205/255.0f alpha:1];
    }
    [textView resignFirstResponder];
    
    activeView = nil;
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    activeView = nil;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }else if (textView.text.length > 149 && ![text isEqualToString:@""]) {
        return NO;
    }
    
    return YES;
}

- (BOOL) textField:(UITextField*)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString*)textEntered {
    
    if (textField.text.length == 0 && ![textEntered isEqualToString:@""]){
        if (textField == txtAddressLine1) {
            txtAddressLine1.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueAddress1.png"]];
        }else if (textField == txtCity) {
            txtCity.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueCity.png"]];
        }else if (textField == txtState) {
            txtState.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueState.png"]];
        }else if (textField == txtPostCode) {
            txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueMail.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        if (textField == txtAddressLine1) {
            txtAddressLine1.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayAddress1.png"]];
        }else if (textField == txtCity) {
            txtCity.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayCity.png"]];
        }else if (textField == txtState) {
            txtState.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayState.png"]];
        }else if (textField == txtPostCode) {
            txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayMail.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        if (textField == txtAddressLine1) {
            txtAddressLine1.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redAddress1.png"]];
        }else if (textField == txtCity) {
            txtCity.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCity.png"]];
        }else if (textField == txtState) {
            txtState.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redState.png"]];
        }else if (textField == txtPostCode) {
            txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redMail.png"]];
        }
    }
    return YES;
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

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

- (IBAction)logSelectedButton:(DLRadioButton *)radiobutton {
    NSLog(@"%@ is selected.\n", radiobutton.selectedButton.titleLabel.text);
    selectedLocation = radiobutton.selectedButton.titleLabel.text;
}

-(IBAction)backAction:(id)sender{
    
    if (fromSettings && addressFromSettings) {
        [self deleteAddress];
    }else if (fromSettings) {
        [self checkFields];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
    
//    [self.delegate performBackFromSignUp2];
}

-(IBAction)nextAction:(id)sender{
    
    [self checkFields];
//    [self performInsertAddress];
}

-(void)checkAddressLine1{
    if ([txtAddressLine1.text length] == 0) {
        [txtAddressLine1 becomeFirstResponder];
        isFirstResponder = YES;
        [self setEmptyTextField:txtAddressLine1];
        txtAddressLine1.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redAddress1.png"]];
        
        isValidationOK = NO;
    }
    [self checkCity];
}

-(void)checkCity{
    if ([txtCity.text length] == 0) {
        if (!isFirstResponder) {
            [txtCity becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtCity];
        txtCity.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redCity.png"]];
        
        isValidationOK = NO;
    }
    
    [self checkState];
}

-(void)checkState{
    if ([txtState.text length] == 0) {
        if (!isFirstResponder) {
            [txtState becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtState];
        txtState.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redState.png"]];
        
        isValidationOK = NO;
    }
    [self checkPostCode];
}

-(void)checkPostCode{
    
    if ([txtPostCode.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location == NSNotFound) {
        int postCode = [txtPostCode.text intValue];
        AppDelegate *appDelegate= (AppDelegate *)[[UIApplication sharedApplication] delegate];
        bool isOk = NO;
        for (NSNumber *postCodeNumber in appDelegate.postalCodes) {
            if (postCode == [postCodeNumber intValue]) {
                isOk = YES;
                break;
            }
        }
        if (isOk) {
            if (isValidationOK) {
                if (fromSettings && addressFromSettings) {
                    [self performUpdateAddress];
                }else{
                    [self performInsertAddress];
                }
            }
        }else{
            txtPostCode.text = @"";
            txtPostCode.placeholder = @"Post code invalid";
            if (!isFirstResponder) {
                [txtPostCode becomeFirstResponder];
                isFirstResponder = YES;
            }
            [self setEmptyTextField:txtPostCode];
            txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redMail.png"]];
        }
        
    }else{
        txtPostCode.text = @"";
        txtPostCode.placeholder = @"Post code invalid";
        if (!isFirstResponder) {
            [txtPostCode becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtPostCode];
        txtPostCode.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redMail.png"]];
    }
}

-(void)performInsertAddress{
    
    PFObject* object = [PFObject objectWithClassName:@"Address"];
    [object setObject:txtAddressLine1.text forKey:@"street"];
    [object setObject:txtAddressLine2.text forKey:@"streetTwo"];
    [object setObject:txtCity.text forKey:@"city"];
    [object setObject:txtState.text forKey:@"state"];
    [object setObject:[NSNumber numberWithInt:[txtPostCode.text intValue]] forKey:@"postCode"];
    [object setObject:selectedLocation forKey:@"location"];
    if (![txtNotes.text isEqualToString:@"Access Notes"] && ![txtNotes.text isEqualToString:@""]) {
        [object setObject:txtNotes.text forKey:@"notes"];
    }  
    
    if (fromSettings) {
        [object setObject:[PFUser currentUser] forKey:@"user"];
        [AddressHelper performInsertAddress:self withAddress:object isUpdate:NO];
    }else{
        if (!signUp3ViewController) {
            signUp3ViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUp3ViewController"];
        }
        signUp3ViewController.signUpUser = signUpUser;
        signUp3ViewController.addressObject = object;
        signUp3ViewController.fromSettings = NO;
        [self.navigationController pushViewController:signUp3ViewController animated:YES];
    }
    
    
//    [self.delegate performNextFromSignUp2:object];
}

-(void)setFromSettingsFields{
    
//    bool homeExist, workExist, otherExist = NO;
    AppDelegate *appDelegate= (AppDelegate *)[[UIApplication sharedApplication] delegate];
    for (PFObject *address in appDelegate.userAddresses) {
        if ([[address objectForKey:@"location"] isEqualToString:@"Home"]) {
            [btnHome removeFromSuperview];
            btnHome = nil;
            if (btnWork) {
                [btnWork setSelected:YES];
                selectedLocation = @"Work";
            }else if (btnOther){
                [btnOther setSelected:YES];
                selectedLocation = @"Other";
            }
            
        }
        if ([[address objectForKey:@"location"] isEqualToString:@"Work"]) {
            [btnWork removeFromSuperview];
            btnWork = nil;
            if (!btnHome) {
                [btnOther setSelected:YES];
                selectedLocation = @"Other";
            }
        }
        if ([[address objectForKey:@"location"] isEqualToString:@"Other"]) {
            [btnOther removeFromSuperview];
            btnOther = nil;
        }
    }
    
    [btnPrevious setBackgroundImage:[UIImage imageNamed:@"place-order-icon.png"] forState:UIControlStateNormal];
    [btnPrevious setBackgroundImage:nil forState:UIControlStateHighlighted];
    lblBtnPreviousTitle.text = @"Save";
    
    [btnNext removeFromSuperview];
    [lblBtnTitle removeFromSuperview];
    [bottomView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[btnPrevious]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(btnPrevious, bottomView)]];
}

-(void)saveFromSettings{
    if ([[SlideNavigationController sharedInstance].viewControllers lastObject] == self) {
        [[SlideNavigationController sharedInstance] popViewControllerAnimated:YES];
    }
    
}

-(void)setAddressFromSettingFields{

    txtAddressLine1.text = [addressFromSettings objectForKey:@"street"];
    txtAddressLine2.text = [addressFromSettings objectForKey:@"streetTwo"];
    txtCity.text = [addressFromSettings objectForKey:@"city"];
    txtState.text = [addressFromSettings objectForKey:@"state"];
    txtPostCode.text = [NSString stringWithFormat:@"%@", [addressFromSettings objectForKey:@"postCode"]];
    txtNotes.text = [addressFromSettings objectForKey:@"notes"];
 
    AppDelegate *appDelegate= (AppDelegate *)[[UIApplication sharedApplication] delegate];
    for (PFObject *address in appDelegate.userAddresses) {
        if ([[address objectForKey:@"location"] isEqualToString:@"Home"] && address != addressFromSettings) {
            [btnHome removeFromSuperview];
            btnHome = nil;
            if (btnWork) {
                [btnWork setSelected:YES];
                selectedLocation = @"Work";
            }else if (btnOther){
                [btnOther setSelected:YES];
                selectedLocation = @"Other";
            }
            
        }
        if ([[address objectForKey:@"location"] isEqualToString:@"Work"] && address != addressFromSettings) {
            [btnWork removeFromSuperview];
            btnWork = nil;
            if (!btnHome) {
                [btnOther setSelected:YES];
                selectedLocation = @"Other";
            }
        }
        if ([[address objectForKey:@"location"] isEqualToString:@"Other"] && address != addressFromSettings) {
            [btnOther removeFromSuperview];
            btnOther = nil;
        }
    }
    
    if ([[addressFromSettings objectForKey:@"location"] isEqualToString:@"Home"]) {
        [btnHome setSelected:YES];
        selectedLocation = @"Home";
    }else if ([[addressFromSettings objectForKey:@"location"] isEqualToString:@"Work"]) {
        [btnWork setSelected:YES];
        selectedLocation = @"Work";
    }else if ([[addressFromSettings objectForKey:@"location"] isEqualToString:@"Other"]) {
        [btnOther setSelected:YES];
        selectedLocation = @"Other";
    }
    
    [btnPrevious setBackgroundImage:[UIImage imageNamed:@"place-order-icon.png"] forState:UIControlStateNormal];
    [btnPrevious setBackgroundImage:nil forState:UIControlStateHighlighted];
    btnPrevious.transform = CGAffineTransformMakeRotation(M_PI);
    btnPrevious.layer.transform = CATransform3DMakeRotation(M_PI,1.0,0.0,0.0);
    lblBtnPreviousTitle.text = @"Delete";
    
    [btnNext setBackgroundImage:[UIImage imageNamed:@"place-order-icon.png"] forState:UIControlStateNormal];
    [btnNext setBackgroundImage:nil forState:UIControlStateHighlighted];
    lblBtnTitle.text = @"Save";
}

-(void)performUpdateAddress{
    [addressFromSettings setObject:txtAddressLine1.text forKey:@"street"];
    [addressFromSettings setObject:txtAddressLine2.text forKey:@"streetTwo"];
    [addressFromSettings setObject:txtCity.text forKey:@"city"];
    [addressFromSettings setObject:txtState.text forKey:@"state"];
    [addressFromSettings setObject:[NSNumber numberWithInt:[txtPostCode.text intValue]] forKey:@"postCode"];
    [addressFromSettings setObject:selectedLocation forKey:@"location"];
    if (![txtNotes.text isEqualToString:@"Access Notes"] && ![txtNotes.text isEqualToString:@""]) {
        [addressFromSettings setObject:txtNotes.text forKey:@"notes"];
    }

    [addressFromSettings setObject:[PFUser currentUser] forKey:@"user"];
    [AddressHelper performInsertAddress:self withAddress:addressFromSettings isUpdate:YES];
}

-(void)deleteAddress{
    AppDelegate *appDelegate= (AppDelegate *)[[UIApplication sharedApplication] delegate];
    if (appDelegate.userAddresses.count > 1) {
        [AddressHelper performDeleteAddress:self withAddress:addressFromSettings];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Info" message:@"You have only one address." delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
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
