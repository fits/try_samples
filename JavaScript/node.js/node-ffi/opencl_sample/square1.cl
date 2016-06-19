__kernel void square(
    __global float* input,
    __global float* output,
    const unsigned int count)
{
  for (int i = 0; i < count; i++) {
    output[i] = input[i] * input[i];
  }
}