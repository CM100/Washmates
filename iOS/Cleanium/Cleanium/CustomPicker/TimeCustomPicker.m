//
//  TimeCustomPicker.m
//  Cleanium
//
//  Created by  DN-117 on 8/3/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "TimeCustomPicker.h"

@implementation TimeCustomPicker

@synthesize selectedTimeIndex;

- (id)init
{
    if (self = [super init]) {

        appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        
        df = [[NSDateFormatter alloc] init];
        NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
        [df setTimeZone:timeZone];
        [df setDateFormat:@"HH:mm"];
    }
    return self;
}

-(void)setTimeArrays:(NSArray *)timeArrayLocal{
    timeArray = timeArrayLocal;
    
    self.selectedTimeDate = [timeArray objectAtIndex:selectedTimeIndex];
//    self.selectedTime = [timeArray objectAtIndex:selectedTimeIndex];
}

/////////////////////////////////////////////////////////////////////////
#pragma mark - ActionSheetCustomPickerDelegate Optional's
/////////////////////////////////////////////////////////////////////////
- (void)configurePickerView:(UIPickerView *)pickerView
{
    // Override default and hide selection indicator
    pickerView.showsSelectionIndicator = NO;
}

- (void)actionSheetPickerDidSucceed:(AbstractActionSheetPicker *)actionSheetPicker origin:(id)origin
{
    [self.delegate timePickerDone:self.selectedTimeDate witTimeIndex:selectedTimeIndex withType:self.type];
}

/////////////////////////////////////////////////////////////////////////
#pragma mark - UIPickerViewDataSource Implementation
/////////////////////////////////////////////////////////////////////////

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    // Returns
    switch (component) {
        case 0: return [timeArray count];
        default:break;
    }
    return 0;
}

/////////////////////////////////////////////////////////////////////////
#pragma mark UIPickerViewDelegate Implementation
/////////////////////////////////////////////////////////////////////////

// returns width of column and height of row for each component.
//- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component
//{
//    switch (component) {
//        case 0: return 160.0f;
//        case 1: return 160.0f;
//        default:break;
//    }
//    
//    return 0;
//}
/*- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
 {
 return
 }
 */
// these methods return either a plain UIString, or a view (e.g UILabel) to display the row for the component.
// for the view versions, we cache any hidden and thus unused views and pass them back for reuse.
// If you return back a different object, the old one will be released. the view will be centered in the row rect
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    switch (component) {
        case 0:{
            NSDate *date = timeArray[(NSUInteger) row];
            NSString *dateString = [df stringFromDate:date];
            
            NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
            NSTimeZone *timeZone = [NSTimeZone timeZoneWithName:@"UTC"];
            [gregorianCalendar setTimeZone:timeZone];
            NSDateComponents *components = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:date];
            components.minute = components.minute + appDelegate.driverInterval;
            
            NSDate *dateEnd = [gregorianCalendar dateFromComponents:components];
            NSString *dateStringEnd = [df stringFromDate:dateEnd];
            
            return [NSString stringWithFormat:@"%@ - %@", dateString, dateStringEnd];
        }
        default:break;
    }
    return nil;
}

/////////////////////////////////////////////////////////////////////////

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    NSLog(@"Row %li selected in component %li", (long)row, (long)component);
    switch (component) {
        case 0:{
            NSDate *date = timeArray[(NSUInteger) row];
            NSString *dateString = [df stringFromDate:date];
            self.selectedTime = dateString;
            self.selectedTimeDate = date;
            selectedTimeIndex = row;
            return;
        }
        default:break;
    }
}

@end
