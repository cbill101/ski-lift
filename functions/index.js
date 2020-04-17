const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
const stripe = require('stripe')(functions.config().stripe.token);
const currency = functions.config().stripe.currency || 'USD';
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// When a user is created, register them with Stripe
exports.createStripeCustomer = functions.auth.user().onCreate(async (user) => {
  const customer = await stripe.accounts.create({
                                                     email: user.email,
                                                     country: 'US',
                                                     type: 'custom',
                                                     requested_capabilities: ['card_payments', 'transfers']
                                                 });
  return admin.firestore().collection('users').doc(user.uid).set({stripe_id: customer.id},{merge: true});
});
