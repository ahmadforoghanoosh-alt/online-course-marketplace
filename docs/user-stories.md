# User Stories: Online Course Marketplace

Milestone: M1. Each story has acceptance criteria that we can check in the running system or in a test.

## US-01 Add a course
As an instructor, I want to add a course with a title and a price, so that students can buy it.

**Acceptance criteria**
- [ ] Given I enter a title and a price greater than 0, when I save, then the course appears in the catalogue with a new id.
- [ ] An empty title or a price of 0 or less is rejected with a message.

## US-02 Browse the catalogue
As a student, I want to see the list of all courses, so that I can choose what to learn.

**Acceptance criteria**
- [ ] When I open the catalogue, I see every course with its id, title, instructor and price.
- [ ] If there are no courses, I see "No courses yet".

## US-03 See course details
As a student, I want to see the details of one course, so that I know what I buy.

**Acceptance criteria**
- [ ] When I enter a course id, I see the title, instructor, description and price.
- [ ] A wrong id shows "Course not found".

## US-04 Add to cart
As a student, I want to add a course to my cart, so that I can buy it later.

**Acceptance criteria**
- [ ] After adding, the course is in the cart.
- [ ] The same course can not be added twice.

## US-05 Remove from cart and undo
As a student, I want to remove a course from my cart and undo my last cart change, so that I can fix a mistake.

**Acceptance criteria**
- [x] After removing, the course is not in the cart.
- [x] After undo, the cart is exactly the same as before the last change.

## US-06 Cart total
As a student, I want to see my cart total, so that I know how much I pay.

**Acceptance criteria**
- [ ] The total is the sum of the course prices.
- [ ] An empty cart shows total 0.

## US-07 Discount code
As a student, I want to use a discount code, so that I pay less.

**Acceptance criteria**
- [x] A valid code lowers the total by its rule.
- [x] An invalid code shows a message and the total does not change.

## US-08 Choose payment method
As a student, I want to pay with Kaspi or with a bank card, so that I can use the method I have.

**Acceptance criteria**
- [ ] I can choose the payment method at checkout.
- [ ] After a successful payment the order status is PAID.
- [ ] After a failed payment the order is not paid and the cart is not cleared.

## US-09 Receipt and enrollment
As a student, I want to get a receipt and be enrolled after I pay, so that I can start the course.

**Acceptance criteria**
- [ ] After payment, I see a receipt with the order id, the courses and the total.
- [ ] The paid courses appear in "My courses".

## US-10 Certificate
As a student, I want to get a certificate when I finish a course, so that I can show my result.

**Acceptance criteria**
- [ ] I can get a certificate only for a course in "My courses" that I marked as completed.
- [ ] The certificate shows my name, the course title and the date.

## US-11 Edit and delete a course
As an instructor, I want to change or remove my course, so that the catalogue always shows correct information.

**Acceptance criteria**
- [ ] Given a course exists, when I change its title, description or price and save, then the catalogue shows the new data.
- [ ] The same checks as for a new course are used: an empty title or a price of 0 or less is rejected with a message.
- [ ] When I delete a course, it is no longer in the catalogue; deleting or changing an unknown id gives "Course not found".
