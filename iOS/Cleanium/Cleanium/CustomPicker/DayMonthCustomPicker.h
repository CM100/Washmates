//
//  DayMonthCustomPicker.h
//  Cleanium
//
//  Created by  DN-117 on 7/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ActionSheetCustomPickerDelegate.h"

@protocol PickupDatePickerDelegate
-(void)pickupPickerDone:(NSString*)selectedDate withMonthIndex:(NSInteger)monthIndex withDayIndex:(NSInteger)dayIndex withType:(NSString*)type;
@end

@interface DayMonthCustomPicker : NSObject<ActionSheetCustomPickerDelegate>
{
    NSArray *monthArray;
    NSArray *dayArray;
    
    NSDateFormatter *df;
    NSDateFormatter *dfSelectedDate;
    
}

@property (nonatomic, weak) id<PickupDatePickerDelegate> delegate;

@property (nonatomic, strong) NSString *selectedMonth;
@property (nonatomic, strong) NSString *selectedDay;
@property (nonatomic, strong) NSString *selectedYear;
@property (nonatomic, strong) NSString *minYear;
@property (nonatomic, strong) NSString *maxYear;
@property (nonatomic, strong) NSMutableDictionary *dateDictionary;
@property (nonatomic, assign) NSInteger selectedMonthIndex;
@property (nonatomic, assign) NSInteger selectedDayIndex;
@property (nonatomic, retain) NSString *type;

-(void)setDateArrays:(NSMutableDictionary *)dateDictionaryLocal;

@end
