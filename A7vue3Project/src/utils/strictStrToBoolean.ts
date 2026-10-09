export default function strictStrToBoolean(value: string | null): boolean {
  if (!value) {
    return false;
  }
  const lowerCaseValue = value.toLowerCase();
  if (lowerCaseValue === 'true') {
    return true;
  } else if (lowerCaseValue === 'false') {
    return false;
  } else {
    throw new Error(`Invalid boolean string: ${value}`);
  }
}