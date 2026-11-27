export const ORDER_VALIDATION = {
  name: {
    maxLength: 100,
  },
  cargo: {
    volume: {
      min: 1,
      max: 3000,
      step: 0.5,
    },
    weight: {
      min: 1,
      max: 1500,
      step: 0.5,
    },
  },
};
