//
//  SignUpViewController.m
//  Cleanium
//
//  Created by  DN-117 on 7/15/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUpViewController.h"
#import "SignUpHelper.h"

@interface SignUpViewController ()

@end

@implementation SignUpViewController

@synthesize fromSettings;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController setNavigationBarHidden:NO];
  
    [self setNavigationBar];
    
    UITapGestureRecognizer *tapBackground = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pressedBackground:)];
    [self.view addGestureRecognizer:tapBackground];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardDidHideNotification object:nil];
    
    [self setTextFields];
    
    if (fromSettings) {
        self.title = @"PROFILE";
        [self setUserFields];
    }else{
        self.title = @"SIGN UP";
    }
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

- (void)pressedBackground:(id)sender {
    [self.view endEditing:YES];
}

-(void)setTextFields{
    
    txtFirstName.rightViewMode = UITextFieldViewModeAlways;
    UIImageView *rightImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayUser.png"]];
    txtFirstName.rightView = rightImageView;
    
    txtLastName.rightViewMode = UITextFieldViewModeAlways;
    txtLastName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayUser.png"]];
    
    txtEmail.rightViewMode = UITextFieldViewModeAlways;
    txtEmail.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayMail.png"]];
    
    txtPhone.rightViewMode = UITextFieldViewModeAlways;
    txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayPhone.png"]];
    
    txtPassword.rightViewMode = UITextFieldViewModeAlways;
    txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayPassword.png"]];
    
    txtConfirmPassword.rightViewMode = UITextFieldViewModeAlways;
    txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayConfirm.png"]];
    
    NSString *lblForBtnTitle;
    if(fromSettings){
        lblForBtnTitle = @"Return";
    }else{
        lblForBtnTitle = @"Next";
    }
    UIToolbar* keyboardDoneButtonView = [[UIToolbar alloc] init];
    [keyboardDoneButtonView sizeToFit];
    UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:lblForBtnTitle
                                                                   style:UIBarButtonItemStyleBordered target:self
                                                                  action:@selector(nextClicked:)];
    [keyboardDoneButtonView setItems:[NSArray arrayWithObjects:flexibleSpace, doneButton, nil]];
    txtPhone.inputAccessoryView = keyboardDoneButtonView;
}

- (void)nextClicked:(id)sender
{
    if (fromSettings) {
        [txtPhone resignFirstResponder];
    }else{
        [txtPassword becomeFirstResponder];
    }
}

-(void)textFieldDidBeginEditing:(UITextField *)sender
{
    activeView = sender;
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    activeView = nil;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == txtFirstName) {
        if (fromSettings) {
            [txtFirstName resignFirstResponder];
        }else{
            [txtLastName becomeFirstResponder];
        }
    }else if (textField == txtLastName) {
        if (fromSettings) {
            [txtLastName resignFirstResponder];
        }else{
            [txtEmail becomeFirstResponder];
        }
    }else if (textField == txtEmail) {
        [txtPhone becomeFirstResponder];
    }else if (textField == txtPhone) {
        [txtPassword becomeFirstResponder];
    }else if (textField == txtPassword) {
        [txtConfirmPassword becomeFirstResponder];
    }else{
        [txtConfirmPassword resignFirstResponder];
        [self checkFields];
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
        if (textField == txtFirstName) {
            txtFirstName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueUser.png"]];
        }else if (textField == txtLastName) {
            txtLastName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueUser.png"]];
        }else if (textField == txtEmail) {
            txtEmail.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueMail.png"]];
        }else if (textField == txtPhone) {
            txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bluePhone.png"]];
        }else if (textField == txtPassword) {
            txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bluePassword.png"]];
        }else if (textField == txtConfirmPassword) {
            txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blueConfirm.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag != 5) {
        if (textField == txtFirstName) {
            txtFirstName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayUser.png"]];
        }else if (textField == txtLastName) {
            txtLastName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayUser.png"]];
        }else if (textField == txtEmail) {
            txtEmail.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayMail.png"]];
        }else if (textField == txtPhone) {
            txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayPhone.png"]];
        }else if (textField == txtPassword) {
            txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayPassword.png"]];
        }else if (textField == txtConfirmPassword) {
            txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayConfirm.png"]];
        }
    }else if (textField.text.length == 1 && [textEntered isEqualToString:@""] && textField.tag == 5){
        if (textField == txtFirstName) {
            txtFirstName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redUser.png"]];
        }else if (textField == txtLastName) {
            txtLastName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redUser.png"]];
        }else if (textField == txtEmail) {
            txtEmail.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redMail.png"]];
        }else if (textField == txtPhone) {
            txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPhone.png"]];
        }else if (textField == txtPassword) {
            txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPassword.png"]];
        }else if (textField == txtConfirmPassword) {
            txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redConfirm.png"]];
        }
    }else if (textField.text.length > 1 && [textEntered isEqualToString:@""] && [textField isSecureTextEntry]){
        if (textField == txtPassword) {
            [self clearSecureTextField:textField withRange:range withText:textEntered isPassword:YES];
        }else if (textField == txtConfirmPassword) {
            [self clearSecureTextField:textField withRange:range withText:textEntered isPassword:NO];
        }
        return NO;
    }
    return YES;
}

-(void)clearSecureTextField:(UITextField*)textField withRange:(NSRange)range withText:(NSString*)textEntered isPassword:(bool)isPassword{
    
    // Save the current text, in case iOS deletes the whole text
    NSString *text = textField.text;

    // Trigger deletion
    [textField deleteBackward];
 
    // iOS deleted the entire string
    if (textField.text.length != text.length - 1)
    {
        if (textField.text.length == 0) {
            if (isPassword) {
                if (textField.tag == 5) {
                    txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPassword.png"]];
                }else{
                    txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayPassword.png"]];
                }
            }else{
                if (textField.tag == 5) {
                    txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redConfirm.png"]];
                }else{
                    txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"grayConfirm.png"]];
                }
            }
            
        }
    }
}

-(IBAction)nextAction:(id)sender{
    
    [self checkFields];
//    [self performSignUp];
    
}

-(void)performSignUp{
    
    PFUser *user = [PFUser user];
    user.username = txtEmail.text;
    user.email = txtEmail.text;
    user.password = txtPassword.text;
    [user setObject:txtFirstName.text forKey:@"fname"];
    [user setObject:txtLastName.text forKey:@"lname"];
    long long phoneNumber = [txtPhone.text longLongValue];
    [user setObject:[NSNumber numberWithLongLong:phoneNumber] forKey:@"phone"];
    
    if (!signUp2ViewController) {
        signUp2ViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUp2ViewController"];
    }
    signUp2ViewController.signUpUser = user;
    signUp2ViewController.fromSettings = NO;
    [self.navigationController pushViewController:signUp2ViewController animated:YES];
    
//    [self.delegate performNextFromSignUp1:user];
}

-(BOOL) validateEmail: (NSString *) candidate {
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex]; //  return 0;
    return [emailTest evaluateWithObject:candidate];
}

-(void)checkFields{
    
    isFirstResponder = NO;
    isValidationOK = YES;
    [self checkFirstName];
}

-(void)checkFirstName{
    if ([txtFirstName.text length] == 0) {
        txtFirstName.placeholder = @"First name";
        [txtFirstName becomeFirstResponder];
        isFirstResponder = YES;
        [self setEmptyTextField:txtFirstName];
        txtFirstName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redUser.png"]];
        
        isValidationOK = NO;
    }
    [self checkLastName];
}

-(void)checkLastName{
    if ([txtLastName.text length] == 0) {
        txtLastName.placeholder = @"Last name";
        if (!isFirstResponder) {
            [txtLastName becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtLastName];
        txtLastName.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redUser.png"]];
        
        isValidationOK = NO;
    }
    [self checkMail];
}

-(void)checkMail{
    if([self validateEmail:txtEmail.text]) {
        [self checkPhone];
    }else{
        txtEmail.text = @"";
        txtEmail.placeholder = @"Email address is not valid";
        if (!isFirstResponder) {
            [txtEmail becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtEmail];
        txtEmail.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redMail.png"]];
        
        isValidationOK = NO;
        
        [self checkPhone];
    }
}

-(void)checkPhone{

    if (txtPhone.text.length == 8) {
        if ([txtPhone.text rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789 "] invertedSet]].location == NSNotFound) {
            if(fromSettings && txtPassword.text.length == 0 && txtConfirmPassword.text.length == 0){
                if(isValidationOK){
                    [self performUpdate];
                }
            }else{
                [self checkPassword];
            }
        }else{
            txtPhone.text = @"";
            txtPhone.placeholder = @"Phone number not valid";
            if (!isFirstResponder) {
                [txtPhone becomeFirstResponder];
                isFirstResponder = YES;
            }
            [self setEmptyTextField:txtPhone];
            txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPhone.png"]];
            
            isValidationOK = NO;
            
            if(!fromSettings){
                [self checkPassword];
            }else if (txtPassword.text.length > 0 || txtConfirmPassword.text.length > 0){
                [self checkPassword];
            }
        }
        
    }else{
        if (txtPhone.text.length < 8) {
            txtPhone.placeholder = @"Phone number is too short";
        }else{
            txtPhone.placeholder = @"Phone number is too long";
        }
        txtPhone.text = @"";
        if (!isFirstResponder) {
            [txtPhone becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtPhone];
        txtPhone.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPhone.png"]];
        
        isValidationOK = NO;
        
        if(!fromSettings){
            [self checkPassword];
        }else if (txtPassword.text.length > 0 || txtConfirmPassword.text.length > 0){
            [self checkPassword];
        }
    }
}

-(void)checkPassword{
    
    if([txtPassword.text isEqualToString:txtConfirmPassword.text]){
        if (txtPassword.text.length > 5) {
            if (isValidationOK) {
                if (fromSettings) {
                    [self performUpdate];
                }else{
                    [self performSignUp];
                }
            }
        }else{
            txtPassword.text = @"";
            txtPassword.placeholder = @"Password is too short";
            if (!isFirstResponder) {
                [txtPassword becomeFirstResponder];
                isFirstResponder = YES;
            }
            [self setEmptyTextField:txtPassword];
            txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPassword.png"]];
            
            txtConfirmPassword.text = @"";
            txtConfirmPassword.placeholder = @"Confirm password is too short";
            [self setEmptyTextField:txtConfirmPassword];
            txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redConfirm.png"]];
        }
        
    }else{
        if (txtPassword.text.length < 6) {
            txtPassword.text = @"";
            txtPassword.placeholder = @"Password is too short";
            if (!isFirstResponder) {
                [txtPassword becomeFirstResponder];
                isFirstResponder = YES;
            }
            [self setEmptyTextField:txtPassword];
            txtPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redPassword.png"]];
        }
        
        txtConfirmPassword.text = @"";
        txtConfirmPassword.placeholder = @"Confirm Password does not match";
        if (!isFirstResponder) {
            [txtConfirmPassword becomeFirstResponder];
            isFirstResponder = YES;
        }
        [self setEmptyTextField:txtConfirmPassword];
        txtConfirmPassword.rightView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"redConfirm.png"]];
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Info", nil) message:NSLocalizedString(@"Password and Confirm Password are not the same.", nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"Ok", nil) otherButtonTitles:nil];
//        [alert show];
    }

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
    UIEdgeInsets contentInsets = UIEdgeInsetsMake (0.0, 0.0, coveredFrame.size.height - 93, 0.0);
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

-(void)setUserFields{
    
    txtFirstName.text = [[PFUser currentUser] objectForKey:@"fname"];
    txtFirstName.returnKeyType = UIReturnKeyDefault;
    txtLastName.text = [[PFUser currentUser] objectForKey:@"lname"];
    txtLastName.returnKeyType = UIReturnKeyDefault;
    txtEmail.text = [[PFUser currentUser] objectForKey:@"email"];
    [txtEmail setEnabled:NO];
    [txtEmail setTextColor:[UIColor colorWithRed:199/255.0 green:199/255.0 blue:205/255.0 alpha:1]];
    NSNumber *phone = [[PFUser currentUser] objectForKey:@"phone"];
    NSString *phoneString = [NSString stringWithFormat:@"%@", phone];
    txtPhone.text = phoneString;
    
    [btnNext setBackgroundImage:[UIImage imageNamed:@"place-order-icon.png"] forState:UIControlStateNormal];
    lblBtnTitle.text = @"Save";
}

-(void)performUpdate{
    
    PFUser *user = [PFUser currentUser];
    user.username = txtEmail.text;
    user.email = txtEmail.text;
    if(txtPassword.text.length > 0){
        user.password = txtPassword.text;
    }
    [user setObject:txtFirstName.text forKey:@"fname"];
    [user setObject:txtLastName.text forKey:@"lname"];
    long long phoneNumber = [txtPhone.text longLongValue];
    [user setObject:[NSNumber numberWithLongLong:phoneNumber] forKey:@"phone"];
    
    [SignUpHelper performUpdateUser:self withUser:user];
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
