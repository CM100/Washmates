//
//  AddressesViewController.m
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "AddressesViewController.h"
#import "AddressCell.h"
#import <Parse/Parse.h>
#import "SignUp2ViewController.h"
#import "SlideNavigationController.h"
#import "ServiceClient.h"

@interface AddressesViewController ()

@end

@implementation AddressesViewController

#define CELL_ROW 50

@synthesize isAddress;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"ADDRESS INFORMATION";
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:self.navigationItem.backBarButtonItem.style target:nil action:nil];
    
    appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    if (!isAddress) {
        [btnAddAddress setTitle:@"ADD NEW CARD" forState:UIControlStateNormal];
    }
}

-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    if (isAddress) {
        tableData = appDelegate.userAddresses;
        heightConstraint.constant = tableData.count * CELL_ROW;
        [mainTable reloadData];
        [self checkToRemoveBtn];
    }else{
        ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
        [serviceClient fetchStripeCardsForIdWithCompletionBlock:^(id objects) {

            tableData = objects;
            heightConstraint.constant = tableData.count * CELL_ROW;
            [mainTable reloadData];
            [self checkToRemoveBtn];
        } failureBlock:^(NSError *error) {
            
        }withView:self.view];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)addAddressAction:(id)sender{
    
    if (isAddress) {
        SignUp2ViewController *signUp2ViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUp2ViewController"];
        signUp2ViewController.fromSettings = YES;
        [[SlideNavigationController sharedInstance] pushViewController:signUp2ViewController animated:YES];
    }else{
        SignUp3ViewController *signUp3ViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUp3ViewController"];
        signUp3ViewController.fromSettings = YES;
        [[SlideNavigationController sharedInstance] pushViewController:signUp3ViewController animated:YES];
    }
}

//-(void)addNewAddress{
//    
//    PFObject *address = [PFObject objectWithClassName:@"Address"];
//    [tableData addObject:address];
//    heightConstraint.constant = tableData.count * CELL_ROW;
//    
//    [mainTable reloadData];
//    
//    [self checkToRemoveBtn];
//}

-(void)checkToRemoveBtn{
    
    if (tableData.count == 3) {
        [btnAddAddress setHidden:YES];
        [lineView setHidden:YES];
    }else if ([btnAddAddress isHidden]){
        [btnAddAddress setHidden:NO];
        [lineView setHidden:NO];
    }
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return tableData.count;
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    NSString *CellIdentifier = @"AddressCell";
    
    
    AddressCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    }
    
    cell.imgView.image = [UIImage imageNamed:@"location-white.png"];
    
    if (isAddress) {
        PFObject *address = [tableData objectAtIndex:indexPath.row];
        cell.lblTitle.text = [address objectForKey:@"location"];
        cell.lblDescription.text = [NSString stringWithFormat:@"%@, %@, %@, %@", [address objectForKey:@"street"], [address objectForKey:@"city"], [address objectForKey:@"state"], [address objectForKey:@"postCode"]];
    }else{
//        PFObject *address = [tableData objectAtIndex:indexPath.row];
//        cell.lblTitle.text = [address objectForKey:@"location"];
//        cell.lblDescription.text = [NSString stringWithFormat:@"%@, %@, %@, %@", [address objectForKey:@"street"], [address objectForKey:@"city"], [address objectForKey:@"state"], [address objectForKey:@"postCode"]];
    }
    
    
    if (!cellColor) {
        cellColor = cell.backgroundColor;
    }
    
    return cell;
    
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    [cell setBackgroundColor:cellColor];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSLog(@"klik");
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    PFObject *address = [tableData objectAtIndex:indexPath.row];
    SignUp2ViewController *signUp2ViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUp2ViewController"];
    signUp2ViewController.fromSettings = YES;
    signUp2ViewController.addressFromSettings = address;
    [[SlideNavigationController sharedInstance] pushViewController:signUp2ViewController animated:YES];
    
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return CELL_ROW;
}

- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    AddressCell *cell = (AddressCell *)[tableView cellForRowAtIndexPath:indexPath];
    cell.imgView.image = [UIImage imageNamed:@"blueLocation.png"];
    cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
//        [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    [cell.lblTitle setTextColor:cellColor];
    [self setCellColor:[UIColor whiteColor] ForCell:cell];
}

- (void)tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    AddressCell *cell = (AddressCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (![cell isSelected]) {
//                [self setCellColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1] ForCell:cell];
        [self setCellColor:cellColor ForCell:cell];
        cell.imgView.image = [UIImage imageNamed:@"location-white.png"];
        cell.imgViewArrow.image = [UIImage imageNamed:@"whiteRightArow.png"];
        [cell.lblTitle setTextColor:[UIColor whiteColor]];
    }
    
    
    //    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

- (void)setCellColor:(UIColor *)color ForCell:(UITableViewCell *)cell {
    cell.contentView.backgroundColor = color;
    cell.backgroundColor = color;
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
