//
//  DayMonthCustomPicker.m
//  Cleanium
//
//  Created by  DN-117 on 7/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "DayMonthCustomPicker.h"

@implementation DayMonthCustomPicker

@synthesize dateDictionary, minYear, maxYear, selectedYear, selectedMonthIndex, selectedDayIndex;

- (id)init
{
    if (self = [super init]) {
        
//        dateDictionary = [[NSMutableDictionary alloc] init];
//        [dateDictionary setObject:[[NSArray alloc] initWithObjects:@"29", @"30", @"31", nil] forKey:@"12"];
//        [dateDictionary setObject:[[NSArray alloc] initWithObjects:@"1", @"2", @"3", @"4", @"5", nil] forKey:@"1"];
        
//        self.minYear = [dateDictionary objectForKey:@"minYear"];
//        self.maxYear = [dateDictionary objectForKey:@"maxYear"];
//        [dateDictionary removeObjectForKey:@"minYear"];
//        [dateDictionary removeObjectForKey:@"maxYear"];
        
//        monthArray = dateDictionary.allKeys;
//        dayArray = [dateDictionary objectForKey:[monthArray objectAtIndex:0]];
        
        df = [[NSDateFormatter alloc] init];
        dfSelectedDate = [[NSDateFormatter alloc] init];
        [dfSelectedDate setDateFormat:@"yyyy-MM-dd"];
    }
    return self;
}

-(void)setDateArrays:(NSMutableDictionary *)dateDictionaryLocal{
    dateDictionary = dateDictionaryLocal;
    
    monthArray = [self sortMonths];
    self.selectedMonth = [monthArray objectAtIndex:selectedMonthIndex];
    NSDictionary *dic = [dateDictionary objectForKey:self.selectedMonth];
    dayArray = [dic objectForKey:@"days"];
    self.selectedDay = [dayArray objectAtIndex:selectedDayIndex];
    self.selectedYear = [dic objectForKey:@"year"];
}

-(NSArray*)sortMonths{
    
    NSArray *sorted = [dateDictionary keysSortedByValueUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        if ([obj1 isKindOfClass:[NSDictionary class]] && [obj2 isKindOfClass:[NSDictionary class]]) {
            NSDictionary *d1 = obj1;
            NSDictionary *d2 = obj2;
            
            if ([[d1 objectForKey:@"orderNumber"] intValue] > [[d2 objectForKey:@"orderNumber"] intValue]) {
                return (NSComparisonResult)NSOrderedDescending;
            } else if ([[d1 objectForKey:@"orderNumber"] intValue] < [[d2 objectForKey:@"orderNumber"] intValue]) {
                return (NSComparisonResult)NSOrderedAscending;
            }
        }
        return (NSComparisonResult)NSOrderedSame;

    }];
//    NSArray *sorted = [months sortedArrayUsingComparator:^(id obj1, id obj2){
//        if ([obj1 isKindOfClass:[NSDictionary class]] && [obj2 isKindOfClass:[NSDictionary class]]) {
//            NSDictionary *d1 = obj1;
//            NSDictionary *d2 = obj2;
//            
//            if ([[d1 objectForKey:@"orderNumber"] intValue] > [[d2 objectForKey:@"orderNumber"] intValue]) {
//                return (NSComparisonResult)NSOrderedAscending;
//            } else if ([[d1 objectForKey:@"orderNumber"] intValue] < [[d2 objectForKey:@"orderNumber"] intValue]) {
//                return (NSComparisonResult)NSOrderedDescending;
//            }
//        }
//        return (NSComparisonResult)NSOrderedSame;
//    }];
    
    return sorted;
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
    
    NSString *resultMessage;
    if (!self.selectedMonth && !self.selectedDay)
    {
        resultMessage = [NSString stringWithFormat:@"%@-%@-%@",
                         self.selectedYear,
                         monthArray[(NSUInteger) [(UIPickerView *) actionSheetPicker.pickerView selectedRowInComponent:0]],
                         dayArray[(NSUInteger) [(UIPickerView *) actionSheetPicker.pickerView selectedRowInComponent:1]]];
    }
    else{
        resultMessage = [NSString stringWithFormat:@"%@-%@-%@",
                         self.selectedYear,
                         self.selectedMonth,
                         self.selectedDay];
    }
    
    [self.delegate pickupPickerDone:resultMessage withMonthIndex:selectedMonthIndex withDayIndex:selectedDayIndex withType:self.type];
}

/////////////////////////////////////////////////////////////////////////
#pragma mark - UIPickerViewDataSource Implementation
/////////////////////////////////////////////////////////////////////////

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 2;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    // Returns
    switch (component) {
        case 0: return [monthArray count];
        case 1: return [dayArray count];
        default:break;
    }
    return 0;
}

/////////////////////////////////////////////////////////////////////////
#pragma mark UIPickerViewDelegate Implementation
/////////////////////////////////////////////////////////////////////////

// returns width of column and height of row for each component.
- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component
{
    switch (component) {
        case 0: return 160.0f;
        case 1: return 160.0f;
        default:break;
    }
    
    return 0;
}
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
            NSString *monthKey = monthArray[(NSUInteger) row];
            NSInteger monthNumber = [monthKey integerValue];
            NSString *monthName = [[df monthSymbols] objectAtIndex:(monthNumber-1)];
            return monthName;
        }
        case 1: return dayArray[(NSUInteger) row];
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
            self.selectedMonth = monthArray[(NSUInteger) row];
            selectedMonthIndex = row;
            NSDictionary *dic = [dateDictionary objectForKey:self.selectedMonth];
            dayArray = [dic objectForKey:@"days"];
            self.selectedDay = dayArray[0];
            selectedDayIndex = 0;
            self.selectedYear = [dic objectForKey:@"year"];
            
            [pickerView reloadComponent:1];
            [pickerView selectRow:0 inComponent:1 animated:YES];
//            if ([self.selectedMonth intValue] + 1 > 12) {
//                self.selectedYear = self.maxYear;
//            }else{
//                self.selectedYear = self.minYear;
//            }
            return;
        }
        case 1:
            self.selectedDay = dayArray[(NSUInteger) row];
            selectedDayIndex = row;
            return;
        default:break;
    }
}

@end
