//
//  SettingsViewController.m
//  Cleanium
//
//  Created by  DN-117 on 8/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SettingsViewController.h"
#import "MenuCell.h"
#import "SignUpViewController.h"
#import "SignUp3ViewController.h"
#import "SlideNavigationController.h"
#import "AddressesViewController.h"

@interface SettingsViewController ()

@end

@implementation SettingsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"SETTINGS";
    
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:self.navigationItem.backBarButtonItem.style target:nil action:nil];
    
    menuData = [[NSMutableArray alloc] init];
    
    NSMutableDictionary *dicHome = [[NSMutableDictionary alloc] init];
    [dicHome setObject:@"Profile Information" forKey:@"title"];
    [dicHome setObject:[UIImage imageNamed:@"settings_profile_icon.png"] forKey:@"image"];
    [dicHome setObject:[UIImage imageNamed:@"blueUser.png"] forKey:@"imageSel"];
    [menuData addObject:dicHome];
    NSMutableDictionary *dicSchadule = [[NSMutableDictionary alloc] init];
    [dicSchadule setObject:@"Address Information" forKey:@"title"];
    [dicSchadule setObject:[UIImage imageNamed:@"location-white.png"] forKey:@"image"];
    [dicSchadule setObject:[UIImage imageNamed:@"blueLocation.png"] forKey:@"imageSel"];
    [menuData addObject:dicSchadule];
    NSMutableDictionary *dicPricing = [[NSMutableDictionary alloc] init];
    [dicPricing setObject:@"Payment Information" forKey:@"title"];
    [dicPricing setObject:[UIImage imageNamed:@"settings_payment_info2.png"] forKey:@"image"];
    [dicPricing setObject:[UIImage imageNamed:@"blueCard.png"] forKey:@"imageSel"];
    [menuData addObject:dicPricing];
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

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return menuData.count;
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    NSString *CellIdentifier = @"MenuCell";
    
    
    MenuCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    }
    
    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
    
    cell.imgView.image = [dic objectForKey:@"image"];
    cell.lblTitle.text = [dic objectForKey:@"title"];
    
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
    
//    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
//    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
//    cell.imgView.image = [dic objectForKey:@"imageSel"];
//    cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
//    [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    
    if (indexPath.row == 0) {
        SignUpViewController *signUpViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"SignUpViewController"];
        signUpViewController.fromSettings = YES;
        [[SlideNavigationController sharedInstance] pushViewController:signUpViewController animated:YES];
    }else if (indexPath.row == 1) {
        AddressesViewController *addressesViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"AddressesViewController"];
        addressesViewController.isAddress = YES;
        [[SlideNavigationController sharedInstance] pushViewController:addressesViewController animated:YES];
    }else if (indexPath.row == 2) {
        AddressesViewController *addressesViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"AddressesViewController"];
        addressesViewController.isAddress = NO;
        [[SlideNavigationController sharedInstance] pushViewController:addressesViewController animated:YES];
    }
}

-(void) tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
//    [tableView deselectRowAtIndexPath:indexPath animated:YES];
//    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
//    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
//    cell.imgView.image = [dic objectForKey:@"image"];
//    cell.imgViewArrow.image = [UIImage imageNamed:@"whiteRightArow.png"];
//    [cell.lblTitle setTextColor:[UIColor whiteColor]];
//    [self setCellColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1] ForCell:cell];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 50;
}

- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
    cell.imgView.image = [dic objectForKey:@"imageSel"];
    cell.imgViewArrow.image = [UIImage imageNamed:@"blueRightArrow.png"];
//    [cell.lblTitle setTextColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1]];
    [cell.lblTitle setTextColor:cellColor];
    [self setCellColor:[UIColor whiteColor] ForCell:cell];
}

- (void)tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    
    MenuCell *cell = (MenuCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (![cell isSelected]) {
//        [self setCellColor:[UIColor colorWithRed:104/255.0f green:186/255.0f blue:219/255.0f alpha:1] ForCell:cell];
        [self setCellColor:cellColor ForCell:cell];
        NSDictionary *dic = [menuData objectAtIndex:indexPath.row];
        cell.imgView.image = [dic objectForKey:@"image"];
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
