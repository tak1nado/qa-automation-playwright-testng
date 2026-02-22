Feature: Validate created Student data

Background: Create booking and open Web form
Given Booking is created.
Given Switch to Storefront cockpit guest user.

Scenario: Check that student submission success pop-up data corresponds submitted data
Given Open Student Registration Form page.
And Generate Student data based on created booking.
When Fill in Student Registration form on Student Registration Form page.
And Click on Submit button on Student Registration Form page.
Then Check that Student Registration submission pop-up is opened.
And Check that student data is correct in Student Registration submission pop-up.