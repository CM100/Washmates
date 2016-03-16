//
//  ScheduleViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/29/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LocationCustomPicker.h"
#import "AppDelegate.h"
#import "ScheduleData.h"
#import "DayMonthCustomPicker.h"
#import "DateHelper.h"
#import "TimeCustomPicker.h"
#import "CustomPopUp.h"

@interface ScheduleViewController : UIViewController<LocactionPickerDelegate, PickupDatePickerDelegate, TimePickerDelegate, PaymentInfoPopUpDelegate>{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet UILabel *lblHome;
    IBOutlet UILabel *lblAddress;
    IBOutlet UISwitch *swServices;
    IBOutlet UILabel *lblPickupDayMonth;
    IBOutlet UILabel *lblPickupDay;
    IBOutlet UILabel *lblPickupTime;
    IBOutlet UILabel *lblDropOffDayMonth;
    IBOutlet UILabel *lblDropOffDay;
    IBOutlet UILabel *lblDropOffTime;
    IBOutlet UITextView *txtNotes;
    
    UIView *activeView;
    
    UIColor *enteredColor;
    UIColor *emptyColor;
    
    AppDelegate *appDelegate;
    
    NSInteger locationSelectedIndex;
    NSNumber *selectedPostCode;
    
    ScheduleData *scheduleData;
    
    CalculatedDate *calcDate;
    
    NSInteger pickUpDaySelectedIndex;
    NSInteger pickUpMonthSelectedIndex;
    
    NSDictionary *dropOffDateDictionary;
    NSInteger dropOffDaySelectedIndex;
    NSInteger dropOffMonthSelectedIndex;
    
    NSArray *timePickUpArray;
    NSInteger pickUpTimeSelectedIndex;
    
    NSDate *selectedPickupDateTime;
    
    NSArray *timeDropOffArray;
    NSInteger dropOffTimeSelectedIndex;
    
    NSDate *selectedDropOffDateTime;
}

@end
