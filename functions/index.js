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
  const account = await stripe.accounts.create(
    {
         email: user.email,
         country: 'US',
         type: 'custom',
         requested_capabilities: ['card_payments', 'transfers']
     }
    );
  const customer = await stripe.customers.create({email:user.email});

  return admin.firestore().collection('users').doc(user.uid).set(
    {stripe_customer_id: customer.id, stripe_account_id: account.id },
    {merge: true}
    );
});

//Creates payment intent
exports.createPaymentIntent = functions.https.onCall(async (data, context) => {
    console.log(data.amount);
    console.log(data.uid)
    var doc = admin.firestore().collection('users').doc(data.uid).get()
      .then(doc => {
        if (!doc.exists) {
            console.log('No such document!');
        } else {
            var cid = doc.get('stripe_customer_id');
            console.log('Document data:', doc.get('stripe_customer_id'));
        }
        return doc;
      })
        .catch(err => {
            console.log('Error getting document', err);
      });

    const paymentIntent = await stripe.paymentIntents.create(
        {
            amount: 2000,
            currency: 'usd'
        }
    );
    
    return {
      client_secret: intent.client_secret,
    };
});

