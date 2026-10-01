const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "gen-lang-client-0387673080";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: emulatorHost,
      port: emulatorPort,
      rules: rules,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated read of merchants is rejected", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("merchants").get());
});

test("Authenticated user can read merchants catalog", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("merchants").get());
});

test("User can create their own merchant booth", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const boothRef = aliceDb.collection("merchants").doc(ALICE_UID);
  await assertSucceeds(
    boothRef.set({
      id: ALICE_UID,
      ownerId: ALICE_UID,
      name: "آلیس رضایی",
      title: "نان سنتی آلیس",
      phone: "09121234567",
      specialty: "نان و شیرینی سنتی",
      location: "تهران",
      rating: 5,
      reviewCount: 1,
    })
  );
});

test("Bob cannot overwrite Alice's merchant booth", async () => {
  const adminDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await adminDb.collection("merchants").doc(ALICE_UID).set({
    id: ALICE_UID,
    ownerId: ALICE_UID,
    name: "آلیس رضایی",
    title: "نان سنتی آلیس",
    phone: "09121234567",
    specialty: "نان و شیرینی سنتی",
    location: "تهران",
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("merchants").doc(ALICE_UID).update({
      title: "هک شده توسط باب",
    })
  );
});

test("Alice can add products to her own merchant booth", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const prodRef = aliceDb.collection("merchants").doc(ALICE_UID).collection("products").doc("prod_1");
  await assertSucceeds(
    prodRef.set({
      id: "prod_1",
      merchantId: ALICE_UID,
      title: "نان اریسی قزوین",
      price: 95000,
      weight: "بسته ۵۰۰ گرمی",
      isAvailable: true,
    })
  );
});

test("Bob cannot add products to Alice's booth", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const prodRef = bobDb.collection("merchants").doc(ALICE_UID).collection("products").doc("prod_hack");
  await assertFails(
    prodRef.set({
      id: "prod_hack",
      merchantId: ALICE_UID,
      title: "محصول غیرمجاز",
      price: 10000,
    })
  );
});

test("User account is isolated and readable only by owner", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const userRef = aliceDb.collection("users").doc(ALICE_UID);
  await assertSucceeds(
    userRef.set({
      id: ALICE_UID,
      phone: "09121234567",
      name: "آلیس",
      role: "کاسب و تولیدکننده",
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});
