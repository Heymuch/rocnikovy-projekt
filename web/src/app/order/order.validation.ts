export const ORDER_VALIDATION = {
  name: {
    maxLength: 100,
  },
  dates: {
    minDaysBeforeDeparture: 7,
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
